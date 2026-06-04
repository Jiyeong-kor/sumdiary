package com.jeong.sumdiary.data.backup

data class GoogleDriveBackupConfiguration(
    val oauthClientId: String
) {
    val isConfigured: Boolean = oauthClientId.isNotBlank()
}
