package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupCloudConnectionResult
import com.jeong.sumdiary.domain.backup.BackupCloudScope
import com.jeong.sumdiary.domain.backup.BackupDeleteResult
import com.jeong.sumdiary.domain.backup.BackupDownloadResult
import com.jeong.sumdiary.domain.backup.BackupEncryptionMetadata
import com.jeong.sumdiary.domain.backup.BackupUploadResult
import com.jeong.sumdiary.domain.backup.EncryptedBackupFile
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.request.HttpResponseData
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GoogleDriveBackupCloudRepositoryTest {
    @Test
    fun connectReturnsConnectedWhenRequiredScopeTokenIsGranted() = runTest {
        val repository = repository(
            tokenProvider = StaticGoogleDriveAccessTokenProvider(requiredScopeToken)
        )

        val result = repository.connect()

        assertIs<BackupCloudConnectionResult.Connected>(result)
        assertTrue(result.session.hasRequiredScope)
    }

    @Test
    fun connectRequestsOnlyGoogleDriveAppDataScope() = runTest {
        val tokenProvider = CapturingGoogleDriveAccessTokenProvider(requiredScopeToken)
        val repository = repository(tokenProvider = tokenProvider)

        repository.connect()

        assertEquals(setOf(BackupCloudScope.GoogleDriveAppData), tokenProvider.requestedScopes)
        assertEquals(listOf(BackupCloudScope.GoogleDriveAppData), BackupCloudScope.entries.toList())
    }

    @Test
    fun uploadReplacesExistingAppDataBackupFile() = runTest {
        val requestedUrls = mutableListOf<String>()
        val repository = repository(
            tokenProvider = StaticGoogleDriveAccessTokenProvider(requiredScopeToken),
            handler = { request ->
                requestedUrls += request.url.toString()
                when {
                    request.url.encodedPath == "/drive/v3/files" -> jsonResponse(
                        """{"files":[{"id":"old-file","name":"sumdiary-backup.json"}]}"""
                    )
                    request.url.encodedPath == "/drive/v3/files/old-file" -> respond(
                        content = "",
                        status = HttpStatusCode.NoContent
                    )
                    request.url.encodedPath == "/upload/drive/v3/files" -> jsonResponse(
                        """{"id":"new-file","name":"sumdiary-backup.json","modifiedTime":"2026-06-04T00:00:00.000Z"}"""
                    )
                    else -> respond("", HttpStatusCode.NotFound)
                }
            }
        )

        val result = repository.upload(sampleEncryptedFile)

        assertIs<BackupUploadResult.Success>(result)
        assertEquals("sumdiary-backup.json", result.fileName)
        assertTrue(requestedUrls.any { "spaces=appDataFolder" in it })
        assertTrue(requestedUrls.any { it.contains("/upload/drive/v3/files?uploadType=multipart") })
    }

    @Test
    fun downloadReturnsLatestEncryptedBackupFromAppDataFolder() = runTest {
        val repository = repository(
            tokenProvider = StaticGoogleDriveAccessTokenProvider(requiredScopeToken),
            handler = { request ->
                when (request.url.encodedPath) {
                    "/drive/v3/files" -> jsonResponse(
                        """{"files":[{"id":"latest-file","name":"sumdiary-backup.json"}]}"""
                    )
                    "/drive/v3/files/latest-file" -> jsonResponse(encryptedFileJson)
                    else -> respond("", HttpStatusCode.NotFound)
                }
            }
        )

        val result = repository.downloadLatest()

        assertIs<BackupDownloadResult.Success>(result)
        assertEquals(sampleEncryptedFile.encryptedPayloadBase64, result.file.encryptedPayloadBase64)
    }

    @Test
    fun deleteReturnsFileNotFoundWhenAppDataFolderHasNoBackup() = runTest {
        val repository = repository(
            tokenProvider = StaticGoogleDriveAccessTokenProvider(requiredScopeToken),
            handler = { request ->
                when (request.url.encodedPath) {
                    "/drive/v3/files" -> jsonResponse("""{"files":[]}""")
                    else -> respond("", HttpStatusCode.NotFound)
                }
            }
        )

        val result = repository.deleteRemoteFile()

        assertEquals(BackupDeleteResult.FileNotFound, result)
    }

    private fun repository(
        tokenProvider: GoogleDriveAccessTokenProvider,
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            { jsonResponse("""{"files":[]}""") }
    ): GoogleDriveBackupCloudRepository =
        GoogleDriveBackupCloudRepository(
            configuration = GoogleDriveBackupConfiguration("client-id"),
            accessTokenProvider = tokenProvider,
            apiClient = GoogleDriveBackupApiClient(
                HttpClient(MockEngine) {
                    engine {
                        addHandler(handler)
                    }
                }
            )
        )

    private fun MockRequestHandleScope.jsonResponse(content: String) =
        respond(
            content = content,
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )

    private class StaticGoogleDriveAccessTokenProvider(
        private val token: GoogleDriveAccessToken?
    ) : GoogleDriveAccessTokenProvider {
        override suspend fun currentAccessToken(): GoogleDriveAccessToken? = token

        override suspend fun requestAccessToken(
            requiredScopes: Set<BackupCloudScope>
        ): GoogleDriveAccessToken? = token
    }

    private class CapturingGoogleDriveAccessTokenProvider(
        private val token: GoogleDriveAccessToken?
    ) : GoogleDriveAccessTokenProvider {
        var requestedScopes: Set<BackupCloudScope> = emptySet()
            private set

        override suspend fun currentAccessToken(): GoogleDriveAccessToken? = token

        override suspend fun requestAccessToken(
            requiredScopes: Set<BackupCloudScope>
        ): GoogleDriveAccessToken? {
            requestedScopes = requiredScopes
            return token
        }
    }

    private companion object {
        val requiredScopeToken = GoogleDriveAccessToken(
            value = "access-token",
            grantedScopes = setOf(BackupCloudScope.GoogleDriveAppData)
        )

        val sampleEncryptedFile = EncryptedBackupFile(
            createdAtEpochMillis = 1_780_000_000_000,
            encryption = BackupEncryptionMetadata(
                algorithm = "AES-GCM",
                keyDerivation = "PBKDF2",
                saltBase64 = "salt",
                nonceBase64 = "nonce",
                iterations = 120_000
            ),
            encryptedPayloadBase64 = "payload"
        )

        val encryptedFileJson = """
            {
              "format":"sumdiary.backup",
              "version":1,
              "provider":"GoogleDrive",
              "createdAtEpochMillis":1780000000000,
              "encryption":{
                "algorithm":"AES-GCM",
                "keyDerivation":"PBKDF2",
                "saltBase64":"salt",
                "nonceBase64":"nonce",
                "iterations":120000
              },
              "encryptedPayloadBase64":"payload"
            }
        """.trimIndent()
    }
}
