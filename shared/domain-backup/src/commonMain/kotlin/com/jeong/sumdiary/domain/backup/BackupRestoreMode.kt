package com.jeong.sumdiary.domain.backup

enum class BackupRestoreMode {
    Merge,
    Replace
}

data class BackupRestoreReport(
    val restoredEntries: Int,
    val restoredSummaries: Int
)
