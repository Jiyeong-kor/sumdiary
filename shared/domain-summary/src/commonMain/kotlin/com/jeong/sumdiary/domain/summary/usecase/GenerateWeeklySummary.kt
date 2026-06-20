package com.jeong.sumdiary.domain.summary.usecase

import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import com.jeong.sumdiary.domain.summary.Summary
import com.jeong.sumdiary.domain.summary.SummaryRepository
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class GenerateWeeklySummary(
    private val diaryRepository: DiaryRepository,
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(anchorDate: LocalDate): Summary {
        val periodStart = anchorDate.minus(DatePeriod(days = anchorDate.dayOfWeek.ordinal))
        val periodEnd = periodStart.plus(DatePeriod(days = 6))
        val entries = collectEntries(periodStart, periodEnd)
        val summary = summaryRepository.summarize(entries)
        return summary.copy(
            type = SummaryType.WEEKLY,
            periodStart = periodStart,
            periodEnd = periodEnd
        )
    }

    private suspend fun collectEntries(start: LocalDate, end: LocalDate): List<DiaryEntry> {
        val result = mutableListOf<DiaryEntry>()
        var cursor = start
        while (cursor <= end) {
            result += diaryRepository.getByDate(cursor)
            cursor = cursor.plus(DatePeriod(days = 1))
        }
        return result
    }
}
