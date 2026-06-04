package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupCloudRepository
import com.jeong.sumdiary.domain.backup.BackupDeleteResult
import com.jeong.sumdiary.domain.backup.BackupDownloadResult
import com.jeong.sumdiary.domain.backup.BackupProvider
import com.jeong.sumdiary.domain.backup.BackupUploadResult
import com.jeong.sumdiary.domain.backup.EncryptedBackupFile

class InMemoryBackupCloudRepository(
    override val provider: BackupProvider = BackupProvider.GoogleDrive,
    private var connected: Boolean = false
) : BackupCloudRepository {
    private var remoteFile: EncryptedBackupFile? = null
    private var uploadCounter = 0L

    fun connectForDevelopment() {
        connected = true
    }

    fun disconnectForDevelopment() {
        connected = false
    }

    override suspend fun upload(file: EncryptedBackupFile): BackupUploadResult {
        if (!connected) return BackupUploadResult.NotConnected
        remoteFile = file
        return BackupUploadResult.Success(
            provider = provider,
            fileName = file.fileName,
            uploadedAtEpochMillis = ++uploadCounter
        )
    }

    override suspend fun downloadLatest(): BackupDownloadResult {
        if (!connected) return BackupDownloadResult.NotConnected
        return remoteFile?.let(BackupDownloadResult::Success)
            ?: BackupDownloadResult.FileNotFound
    }

    override suspend fun deleteRemoteFile(): BackupDeleteResult {
        if (!connected) return BackupDeleteResult.NotConnected
        if (remoteFile == null) return BackupDeleteResult.FileNotFound
        remoteFile = null
        return BackupDeleteResult.Success(provider)
    }
}
