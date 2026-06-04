package com.jeong.sumdiary.feature.backup

enum class BackupUiStatus {
    NotConnected,
    Ready,
    Running,
    Success,
    NeedsConnection,
    Failed
}

data class BackupState(
    val providerName: String,
    val connected: Boolean,
    val status: BackupUiStatus,
    val statusTitle: String,
    val statusDescription: String,
    val lastResult: String?,
    val busy: Boolean
) {
    companion object {
        fun initial(providerName: String): BackupState = BackupState(
            providerName = providerName,
            connected = false,
            status = BackupUiStatus.NotConnected,
            statusTitle = "$providerName 연결 필요",
            statusDescription = "백업은 사용자가 직접 켠 뒤에만 동작해요.",
            lastResult = null,
            busy = false
        )
    }
}
