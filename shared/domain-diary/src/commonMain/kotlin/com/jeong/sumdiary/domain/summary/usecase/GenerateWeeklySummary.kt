package com.jeong.sumdiary.domain.summary.usecase

import com.jeong.sumdiary.domain.diary.repository.DiaryRepository
import com.jeong.sumdiary.domain.summary.model.Summary
import com.jeong.sumdiary.domain.summary.model.SummaryType
import com.jeong.sumdiary.domain.summary.repository.SummaryRepository
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class GenerateWeeklySummary(
    private val diaryRepository: DiaryRepository,
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(weekStart: LocalDate): Summary {
        val weekEnd = weekStart.plus(DatePeriod(days = 6))
        val entries = buildList {
            var current = weekStart
            while (current <= weekEnd) {
                addAll(diaryRepository.getByDate(current))
                current = current.plus(DatePeriod(days = 1))
            }
        }
        val baseSummary = summaryRepository.summarize(entries)
        return baseSummary.copy(
            type = SummaryType.WEEKLY,
            periodStart = weekStart,
            periodEnd = weekEnd
        )
    }
}
