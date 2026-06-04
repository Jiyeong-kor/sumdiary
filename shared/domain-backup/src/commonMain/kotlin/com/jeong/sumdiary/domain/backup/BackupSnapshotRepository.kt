package com.jeong.sumdiary.domain.backup

interface BackupSnapshotRepository {
    suspend fun buildSnapshot(): BackupSnapshot
    suspend fun restoreSnapshot(
        snapshot: BackupSnapshot,
        mode: BackupRestoreMode
    ): BackupRestoreReport
}
