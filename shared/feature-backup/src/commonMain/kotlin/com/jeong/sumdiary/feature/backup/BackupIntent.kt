package com.jeong.sumdiary.feature.backup

sealed interface BackupIntent {
    data object ConnectGoogleDrive : BackupIntent
    data object RunManualBackup : BackupIntent
    data object RestoreMerge : BackupIntent
    data object DeleteRemoteBackup : BackupIntent
}
