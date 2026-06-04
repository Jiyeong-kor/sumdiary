package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupCloudConnectionResult
import com.jeong.sumdiary.domain.backup.BackupCloudRepository
import com.jeong.sumdiary.domain.backup.BackupCloudScope
import com.jeong.sumdiary.domain.backup.BackupCloudSession
import com.jeong.sumdiary.domain.backup.BackupCloudSessionState
import com.jeong.sumdiary.domain.backup.BackupDeleteResult
import com.jeong.sumdiary.domain.backup.BackupDownloadResult
import com.jeong.sumdiary.domain.backup.BackupProvider
import com.jeong.sumdiary.domain.backup.BackupUploadResult
import com.jeong.sumdiary.domain.backup.EncryptedBackupFile
import io.ktor.client.HttpClient

class GoogleDriveBackupCloudRepository(
    private val configuration: GoogleDriveBackupConfiguration,
    private val accessTokenProvider: GoogleDriveAccessTokenProvider = UnavailableGoogleDriveAccessTokenProvider,
    private val apiClient: GoogleDriveBackupApiClient = GoogleDriveBackupApiClient(HttpClient())
) : BackupCloudRepository {
    override val provider: BackupProvider = BackupProvider.GoogleDrive

    override suspend fun connect(): BackupCloudConnectionResult {
        if (!configuration.isConfigured) {
            return BackupCloudConnectionResult.Failed
        }
        val token = accessTokenProvider.requestAccessToken(RequiredScopes)
            ?: return BackupCloudConnectionResult.Failed
        val session = token.toSession()
        return if (session.hasRequiredScope) {
            BackupCloudConnectionResult.Connected(session)
        } else {
            BackupCloudConnectionResult.MissingRequiredScope(
                session.copy(state = BackupCloudSessionState.MissingRequiredScope)
            )
        }
    }

    override suspend fun currentSession(): BackupCloudSession {
        if (!configuration.isConfigured) {
            return BackupCloudSession(
                provider = provider,
                state = BackupCloudSessionState.NotConnected
            )
        }
        return accessTokenProvider.currentAccessToken()
            ?.toSession()
            ?: BackupCloudSession(
                provider = provider,
                state = BackupCloudSessionState.NotConnected
            )
    }

    override suspend fun upload(file: EncryptedBackupFile): BackupUploadResult {
        val token = connectedTokenOrNull() ?: return BackupUploadResult.NotConnected
        return when (val result = apiClient.upload(token, file)) {
            is GoogleDriveApiResult.Success -> BackupUploadResult.Success(
                provider = provider,
                fileName = result.value.name,
                uploadedAtEpochMillis = file.createdAtEpochMillis
            )
            GoogleDriveApiResult.Failed -> BackupUploadResult.Failed
        }
    }

    override suspend fun downloadLatest(): BackupDownloadResult {
        val token = connectedTokenOrNull() ?: return BackupDownloadResult.NotConnected
        return when (val result = apiClient.downloadLatest(token)) {
            is GoogleDriveApiResult.Success -> result.value
                ?.let { BackupDownloadResult.Success(it) }
                ?: BackupDownloadResult.FileNotFound
            GoogleDriveApiResult.Failed -> BackupDownloadResult.Failed
        }
    }

    override suspend fun deleteRemoteFile(): BackupDeleteResult {
        val token = connectedTokenOrNull() ?: return BackupDeleteResult.NotConnected
        return when (val result = apiClient.deleteLatest(token)) {
            is GoogleDriveApiResult.Success -> if (result.value) {
                BackupDeleteResult.Success(provider)
            } else {
                BackupDeleteResult.FileNotFound
            }
            GoogleDriveApiResult.Failed -> BackupDeleteResult.DeleteFailed
        }
    }

    private suspend fun connectedTokenOrNull(): GoogleDriveAccessToken? =
        accessTokenProvider.currentAccessToken()
            ?.takeIf { configuration.isConfigured && it.hasRequiredScopes }

    private fun GoogleDriveAccessToken.toSession(): BackupCloudSession =
        BackupCloudSession(
            provider = provider,
            state = if (hasRequiredScopes) {
                BackupCloudSessionState.Connected
            } else {
                BackupCloudSessionState.MissingRequiredScope
            },
            grantedScopes = grantedScopes
        )

    companion object {
        val RequiredScopes: Set<BackupCloudScope> = setOf(BackupCloudScope.GoogleDriveAppData)
    }
}
