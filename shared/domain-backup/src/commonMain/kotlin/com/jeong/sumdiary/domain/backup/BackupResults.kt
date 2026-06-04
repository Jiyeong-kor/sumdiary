package com.jeong.sumdiary.domain.backup

sealed interface BackupRunResult {
    data class Success(
        val provider: BackupProvider,
        val fileName: String,
        val uploadedAtEpochMillis: Long
    ) : BackupRunResult

    data object NotConnected : BackupRunResult
    data object EncryptionFailed : BackupRunResult
    data object UploadFailed : BackupRunResult
}

sealed interface BackupRestoreResult {
    data class Success(
        val provider: BackupProvider,
        val restoredEntries: Int,
        val restoredSummaries: Int
    ) : BackupRestoreResult

    data object NotConnected : BackupRestoreResult
    data object FileNotFound : BackupRestoreResult
    data object InvalidPassphrase : BackupRestoreResult
    data object UnsupportedVersion : BackupRestoreResult
    data object CorruptedFile : BackupRestoreResult
}

sealed interface BackupDeleteResult {
    data class Success(
        val provider: BackupProvider
    ) : BackupDeleteResult

    data object NotConnected : BackupDeleteResult
    data object FileNotFound : BackupDeleteResult
    data object DeleteFailed : BackupDeleteResult
}

sealed interface BackupUploadResult {
    data class Success(
        val provider: BackupProvider,
        val fileName: String,
        val uploadedAtEpochMillis: Long
    ) : BackupUploadResult

    data object NotConnected : BackupUploadResult
    data object Failed : BackupUploadResult
}

sealed interface BackupDownloadResult {
    data class Success(val file: EncryptedBackupFile) : BackupDownloadResult
    data object NotConnected : BackupDownloadResult
    data object FileNotFound : BackupDownloadResult
    data object Failed : BackupDownloadResult
}
