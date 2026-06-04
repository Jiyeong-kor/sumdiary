package com.jeong.sumdiary.domain.backup

enum class BackupProvider(
    val id: String,
    val displayName: String
) {
    GoogleDrive(
        id = "google_drive",
        displayName = "Google Drive"
    )
}
