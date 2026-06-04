package com.jeong.sumdiary.domain.summary.usecase

import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import com.jeong.sumdiary.domain.summary.SummaryGenerationResult
import com.jeong.sumdiary.domain.summary.SummaryRepository
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber

class GenerateWeeklySummary(
    private val diaryRepository: DiaryRepository,
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(anchorDate: LocalDate): SummaryGenerationResult {
        val periodStart = anchorDate.minusDays(anchorDate.dayOfWeek.isoDayNumber - 1)
        val periodEnd = periodStart.plusDays(6)
        val entries = collectEntries(periodStart, periodEnd)
        if (entries.isEmpty()) {
            return SummaryGenerationResult.NoEntries(
                periodStart = periodStart,
                periodEnd = periodEnd
            )
        }
        val summary = summaryRepository.summarize(entries)
        return SummaryGenerationResult.Success(
            summary.copy(
                type = SummaryType.WEEKLY,
                periodStart = periodStart,
                periodEnd = periodEnd
            )
        )
    }

    private suspend fun collectEntries(start: LocalDate, end: LocalDate): List<DiaryEntry> {
        val result = mutableListOf<DiaryEntry>()
        var cursor = start
        while (cursor <= end) {
            result += diaryRepository.getByDate(cursor)
            cursor = cursor.plusDays(1)
        }
        return result
    }

    private fun LocalDate.plusDays(days: Int): LocalDate =
        LocalDate.fromEpochDays(toEpochDays() + days)

    private fun LocalDate.minusDays(days: Int): LocalDate =
        LocalDate.fromEpochDays(toEpochDays() - days)
}
