package com.jeong.sumdiary.domain.backup

interface BackupCloudRepository {
    val provider: BackupProvider

    suspend fun connect(): BackupCloudConnectionResult
    suspend fun currentSession(): BackupCloudSession
    suspend fun upload(file: EncryptedBackupFile): BackupUploadResult
    suspend fun downloadLatest(): BackupDownloadResult
    suspend fun deleteRemoteFile(): BackupDeleteResult
}
