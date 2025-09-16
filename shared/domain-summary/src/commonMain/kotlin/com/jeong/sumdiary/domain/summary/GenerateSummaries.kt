package com.jeong.sumdiary.domain.summary

import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class GenerateDailySummary(
    private val diaryRepository: DiaryRepository,
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(date: LocalDate): Summary {
        val entries = diaryRepository.getByDate(date)
        val generated = summaryRepository.summarize(entries)
        return generated.copy(
            type = SummaryType.DAILY,
            periodStart = date,
            periodEnd = date,
        )
    }
}

class GenerateWeeklySummary(
    private val diaryRepository: DiaryRepository,
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(periodStart: LocalDate): Summary {
        val periodEnd = periodStart.plus(DatePeriod(days = 6))
        val entries = diaryRepository.observeRange(periodStart, periodEnd).first()
        val generated = summaryRepository.summarize(entries)
        return generated.copy(
            type = SummaryType.WEEKLY,
            periodStart = periodStart,
            periodEnd = periodEnd,
        )
    }
}
