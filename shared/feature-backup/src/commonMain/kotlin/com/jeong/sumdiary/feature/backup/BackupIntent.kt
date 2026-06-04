package com.jeong.sumdiary.feature.backup

import com.jeong.sumdiary.domain.backup.BackupPassphrase

sealed interface BackupIntent {
    data object ConnectGoogleDrive : BackupIntent
    data class RunManualBackup(val passphrase: BackupPassphrase) : BackupIntent
    data class RestoreMerge(val passphrase: BackupPassphrase) : BackupIntent
    data object DeleteRemoteBackup : BackupIntent
}
