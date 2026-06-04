package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.data.diary.db.Diary
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.summary.db.Summary as SummaryRow
import com.jeong.sumdiary.data.summary.db.SummaryDatabase
import com.jeong.sumdiary.domain.backup.BackupRestoreMode
import com.jeong.sumdiary.domain.backup.BackupRestoreReport
import com.jeong.sumdiary.domain.backup.BackupSnapshot
import com.jeong.sumdiary.domain.backup.BackupSnapshotRepository
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.summary.Summary
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class DatabaseBackupSnapshotRepository(
    private val diaryDatabase: DiaryDatabase,
    private val summaryDatabase: SummaryDatabase,
    private val dispatcher: CoroutineDispatcher
) : BackupSnapshotRepository {

    @OptIn(ExperimentalTime::class)
    override suspend fun buildSnapshot(): BackupSnapshot = withContext(dispatcher) {
        BackupSnapshot(
            createdAtEpochMillis = Clock.System.now().toEpochMilliseconds(),
            entries = diaryDatabase.diaryQueries.selectAll()
                .executeAsList()
                .map { it.toDiaryEntry() },
            summaries = summaryDatabase.summaryQueries.selectAll()
                .executeAsList()
                .map { it.toSummary() }
        )
    }

    override suspend fun restoreSnapshot(
        snapshot: BackupSnapshot,
        mode: BackupRestoreMode
    ): BackupRestoreReport = withContext(dispatcher) {
        if (mode == BackupRestoreMode.Replace) {
            summaryDatabase.summaryQueries.deleteAll()
            diaryDatabase.diaryQueries.deleteAll()
        }

        snapshot.entries.forEach { entry ->
            diaryDatabase.diaryQueries.upsertDiary(
                id = entry.id,
                date = entry.date.toString(),
                time = entry.time.toString(),
                content = entry.content
            )
        }

        snapshot.summaries.forEach { summary ->
            summaryDatabase.summaryQueries.upsertSummary(
                type = summary.type.name,
                periodStart = summary.periodStart.toString(),
                periodEnd = summary.periodEnd.toString(),
                summaryText = summary.text
            )
        }

        BackupRestoreReport(
            restoredEntries = snapshot.entries.size,
            restoredSummaries = snapshot.summaries.size
        )
    }

    private fun Diary.toDiaryEntry(): DiaryEntry =
        DiaryEntry(
            id = id,
            date = LocalDate.parse(date),
            time = LocalTime.parse(time),
            content = content
        )

    private fun SummaryRow.toSummary(): Summary =
        Summary(
            type = SummaryType.valueOf(type),
            periodStart = LocalDate.parse(periodStart),
            periodEnd = LocalDate.parse(periodEnd),
            text = summaryText,
            emotions = deriveEmotions(summaryText)
        )

    private fun deriveEmotions(text: String): List<String> {
        val lower = text.lowercase()
        val emotions = mutableListOf<String>()
        if (listOf("행복", "기쁨", "즐거움").any { it in text }) {
            emotions += "positive"
        }
        if (listOf("슬픔", "우울", "피곤").any { it in text }) {
            emotions += "sad"
        }
        if (lower.contains("화나")) {
            emotions += "angry"
        }
        return emotions.ifEmpty { listOf("neutral") }
    }
}
