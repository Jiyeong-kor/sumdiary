package com.jeong.sumdiary.domain.backup

data class BackupCloudSession(
    val provider: BackupProvider,
    val state: BackupCloudSessionState,
    val grantedScopes: Set<BackupCloudScope> = emptySet(),
    val remoteFolder: BackupRemoteFolder? = null,
    val remoteFile: BackupRemoteFile? = null
) {
    val isConnected: Boolean = state == BackupCloudSessionState.Connected
    val hasRequiredScope: Boolean = BackupCloudScope.GoogleDriveAppData in grantedScopes
}

enum class BackupCloudSessionState {
    NotConnected,
    Connected,
    MissingRequiredScope,
    Failed
}

enum class BackupCloudScope(val value: String) {
    GoogleDriveAppData("https://www.googleapis.com/auth/drive.appdata")
}

data class BackupRemoteFolder(
    val provider: BackupProvider,
    val id: String,
    val displayName: String
)

data class BackupRemoteFile(
    val provider: BackupProvider,
    val id: String,
    val fileName: String,
    val updatedAtEpochMillis: Long? = null
)

sealed interface BackupCloudConnectionResult {
    data class Connected(val session: BackupCloudSession) : BackupCloudConnectionResult
    data class MissingRequiredScope(val session: BackupCloudSession) : BackupCloudConnectionResult
    data object Cancelled : BackupCloudConnectionResult
    data object Failed : BackupCloudConnectionResult
}
