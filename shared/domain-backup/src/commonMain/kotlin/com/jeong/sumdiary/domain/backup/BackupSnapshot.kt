package com.jeong.sumdiary.domain.backup

import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.summary.Summary

data class BackupSnapshot(
    val version: Int = BackupConstants.CurrentVersion,
    val createdAtEpochMillis: Long,
    val entries: List<DiaryEntry>,
    val summaries: List<Summary>,
    val settings: BackupSnapshotSettings = BackupSnapshotSettings()
)

data class BackupSnapshotSettings(
    val aiSummaryEnabled: Boolean = true,
    val backupEnabled: Boolean = false
)
