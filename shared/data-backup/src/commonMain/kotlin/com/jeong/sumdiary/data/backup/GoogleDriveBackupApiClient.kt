package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupEncryptionMetadata
import com.jeong.sumdiary.domain.backup.BackupProvider
import com.jeong.sumdiary.domain.backup.EncryptedBackupFile
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GoogleDriveBackupApiClient(
    private val httpClient: HttpClient,
    private val json: Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
) {
    suspend fun upload(accessToken: GoogleDriveAccessToken, file: EncryptedBackupFile): GoogleDriveApiResult<GoogleDriveFile> {
        if (!deleteExistingBackup(accessToken)) {
            return GoogleDriveApiResult.Failed
        }

        val metadata = GoogleDriveFileMetadata(
            name = file.fileName,
            parents = listOf(AppDataFolder),
            mimeType = BackupMimeType
        )
        val response = httpClient.post("$UploadBaseUrl/files") {
            bearer(accessToken)
            parameter("uploadType", "multipart")
            parameter("fields", "id,name,modifiedTime")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "metadata",
                            value = json.encodeToString(metadata),
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                            }
                        )
                        append(
                            key = "file",
                            value = json.encodeToString(file.toPayload()).encodeToByteArray(),
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, BackupMimeType)
                            }
                        )
                    }
                )
            )
        }

        if (!response.status.isSuccess()) {
            return GoogleDriveApiResult.Failed
        }
        return GoogleDriveApiResult.Success(json.decodeFromString(response.bodyAsText()))
    }

    suspend fun downloadLatest(accessToken: GoogleDriveAccessToken): GoogleDriveApiResult<EncryptedBackupFile?> {
        val file = findLatestBackupFile(accessToken) ?: return GoogleDriveApiResult.Success(null)
        val response = httpClient.get("$DriveBaseUrl/files/${file.id}") {
            bearer(accessToken)
            parameter("alt", "media")
        }
        if (!response.status.isSuccess()) {
            return GoogleDriveApiResult.Failed
        }
        val payload = json.decodeFromString<EncryptedBackupFilePayload>(response.bodyAsText())
        return GoogleDriveApiResult.Success(payload.toDomain(file.name))
    }

    suspend fun deleteLatest(accessToken: GoogleDriveAccessToken): GoogleDriveApiResult<Boolean> {
        val file = findLatestBackupFile(accessToken) ?: return GoogleDriveApiResult.Success(false)
        val response = httpClient.delete("$DriveBaseUrl/files/${file.id}") {
            bearer(accessToken)
        }
        return when {
            response.status == HttpStatusCode.NoContent -> GoogleDriveApiResult.Success(true)
            response.status == HttpStatusCode.NotFound -> GoogleDriveApiResult.Success(false)
            else -> GoogleDriveApiResult.Failed
        }
    }

    private suspend fun deleteExistingBackup(accessToken: GoogleDriveAccessToken): Boolean {
        return when (deleteLatest(accessToken)) {
            is GoogleDriveApiResult.Success -> true
            GoogleDriveApiResult.Failed -> false
        }
    }

    private suspend fun findLatestBackupFile(accessToken: GoogleDriveAccessToken): GoogleDriveFile? {
        val response = httpClient.get("$DriveBaseUrl/files") {
            bearer(accessToken)
            parameter("spaces", AppDataFolder)
            parameter("q", "name = '$BackupFileName' and trashed = false")
            parameter("orderBy", "modifiedTime desc")
            parameter("pageSize", "1")
            parameter("fields", "files(id,name,modifiedTime)")
        }
        if (!response.status.isSuccess()) {
            return null
        }
        return json.decodeFromString<GoogleDriveFileList>(response.bodyAsText()).files.firstOrNull()
    }

    private fun io.ktor.client.request.HttpRequestBuilder.bearer(accessToken: GoogleDriveAccessToken) {
        header(HttpHeaders.Authorization, "Bearer ${accessToken.value}")
    }

    private fun HttpStatusCode.isSuccess(): Boolean = value in 200..299

    private fun EncryptedBackupFile.toPayload(): EncryptedBackupFilePayload =
        EncryptedBackupFilePayload(
            format = format,
            version = version,
            provider = provider.name,
            createdAtEpochMillis = createdAtEpochMillis,
            encryption = encryption.toPayload(),
            encryptedPayloadBase64 = encryptedPayloadBase64
        )

    private fun BackupEncryptionMetadata.toPayload(): BackupEncryptionMetadataPayload =
        BackupEncryptionMetadataPayload(
            algorithm = algorithm,
            keyDerivation = keyDerivation,
            saltBase64 = saltBase64,
            nonceBase64 = nonceBase64,
            iterations = iterations
        )

    private fun EncryptedBackupFilePayload.toDomain(fileName: String): EncryptedBackupFile =
        EncryptedBackupFile(
            format = format,
            version = version,
            fileName = fileName,
            provider = BackupProvider.valueOf(provider),
            createdAtEpochMillis = createdAtEpochMillis,
            encryption = encryption.toDomain(),
            encryptedPayloadBase64 = encryptedPayloadBase64
        )

    private fun BackupEncryptionMetadataPayload.toDomain(): BackupEncryptionMetadata =
        BackupEncryptionMetadata(
            algorithm = algorithm,
            keyDerivation = keyDerivation,
            saltBase64 = saltBase64,
            nonceBase64 = nonceBase64,
            iterations = iterations
        )

    companion object {
        private const val DriveBaseUrl = "https://www.googleapis.com/drive/v3"
        private const val UploadBaseUrl = "https://www.googleapis.com/upload/drive/v3"
        private const val AppDataFolder = "appDataFolder"
        private const val BackupFileName = "sumdiary-backup.json"
        private const val BackupMimeType = "application/vnd.sumdiary.backup+json"
    }
}

sealed interface GoogleDriveApiResult<out T> {
    data class Success<T>(val value: T) : GoogleDriveApiResult<T>
    data object Failed : GoogleDriveApiResult<Nothing>
}

@Serializable
private data class GoogleDriveFileMetadata(
    val name: String,
    val parents: List<String>,
    val mimeType: String
)

@Serializable
data class GoogleDriveFile(
    val id: String,
    val name: String,
    val modifiedTime: String? = null
)

@Serializable
private data class GoogleDriveFileList(
    val files: List<GoogleDriveFile> = emptyList()
)

@Serializable
private data class EncryptedBackupFilePayload(
    val format: String,
    val version: Int,
    val provider: String,
    val createdAtEpochMillis: Long,
    val encryption: BackupEncryptionMetadataPayload,
    val encryptedPayloadBase64: String
)

@Serializable
private data class BackupEncryptionMetadataPayload(
    val algorithm: String,
    val keyDerivation: String,
    val saltBase64: String,
    val nonceBase64: String,
    val iterations: Int
)
