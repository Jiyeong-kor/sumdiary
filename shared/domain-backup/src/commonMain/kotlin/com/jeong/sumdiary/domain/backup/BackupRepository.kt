package com.jeong.sumdiary.domain.backup

interface BackupRepository {
    suspend fun runBackup(passphrase: BackupPassphrase): BackupRunResult
    suspend fun restoreBackup(
        passphrase: BackupPassphrase,
        mode: BackupRestoreMode
    ): BackupRestoreResult

    suspend fun deleteRemoteBackup(): BackupDeleteResult
}
