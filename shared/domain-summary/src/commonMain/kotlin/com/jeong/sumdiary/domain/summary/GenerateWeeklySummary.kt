package com.jeong.sumdiary.domain.summary

import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class GenerateWeeklySummary(
    private val diaryRepository: DiaryRepository,
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(weekStart: LocalDate): Summary {
        val weekEnd = weekStart.plus(DatePeriod(days = 6))
        val entries = diaryRepository.observeRange(weekStart, weekEnd).first()
        return summaryRepository.summarize(entries)
    }
}
