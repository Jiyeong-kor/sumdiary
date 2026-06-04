package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupRestoreMode
import com.jeong.sumdiary.domain.backup.BackupRestoreReport
import com.jeong.sumdiary.domain.backup.BackupSnapshot
import com.jeong.sumdiary.domain.backup.BackupSnapshotRepository

class DevelopmentBackupSnapshotRepository : BackupSnapshotRepository {
    private var latestSnapshot = BackupSnapshot(
        createdAtEpochMillis = 0L,
        entries = emptyList(),
        summaries = emptyList()
    )

    override suspend fun buildSnapshot(): BackupSnapshot = latestSnapshot

    override suspend fun restoreSnapshot(
        snapshot: BackupSnapshot,
        mode: BackupRestoreMode
    ): BackupRestoreReport {
        latestSnapshot = when (mode) {
            BackupRestoreMode.Merge -> latestSnapshot.copy(
                entries = (latestSnapshot.entries + snapshot.entries).distinctBy { it.id },
                summaries = latestSnapshot.summaries + snapshot.summaries
            )
            BackupRestoreMode.Replace -> snapshot
        }
        return BackupRestoreReport(
            restoredEntries = snapshot.entries.size,
            restoredSummaries = snapshot.summaries.size
        )
    }
}
