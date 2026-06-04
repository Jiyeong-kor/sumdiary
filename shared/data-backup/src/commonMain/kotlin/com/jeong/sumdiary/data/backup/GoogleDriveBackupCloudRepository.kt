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

class GoogleDriveBackupCloudRepository(
    private val configuration: GoogleDriveBackupConfiguration
) : BackupCloudRepository {
    override val provider: BackupProvider = BackupProvider.GoogleDrive

    override suspend fun connect(): BackupCloudConnectionResult {
        if (!configuration.isConfigured) {
            return BackupCloudConnectionResult.Failed
        }
        return BackupCloudConnectionResult.MissingRequiredScope(
            currentSession().copy(state = BackupCloudSessionState.MissingRequiredScope)
        )
    }

    override suspend fun currentSession(): BackupCloudSession =
        BackupCloudSession(
            provider = provider,
            state = if (configuration.isConfigured) {
                BackupCloudSessionState.MissingRequiredScope
            } else {
                BackupCloudSessionState.NotConnected
            },
            grantedScopes = emptySet()
        )

    override suspend fun upload(file: EncryptedBackupFile): BackupUploadResult =
        BackupUploadResult.NotConnected

    override suspend fun downloadLatest(): BackupDownloadResult =
        BackupDownloadResult.NotConnected

    override suspend fun deleteRemoteFile(): BackupDeleteResult =
        BackupDeleteResult.NotConnected

    companion object {
        val RequiredScopes: Set<BackupCloudScope> = setOf(BackupCloudScope.GoogleDriveAppData)
    }
}
