package com.jeong.sumdiary.domain.summary.usecase

import com.jeong.sumdiary.domain.diary.DiaryRepository
import com.jeong.sumdiary.domain.summary.Summary
import com.jeong.sumdiary.domain.summary.SummaryRepository
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.datetime.LocalDate

class GenerateDailySummary(
    private val diaryRepository: DiaryRepository,
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(date: LocalDate): Summary {
        val entries = diaryRepository.getByDate(date)
        val summary = summaryRepository.summarize(entries)
        return summary.copy(
            type = SummaryType.DAILY,
            periodStart = date,
            periodEnd = date
        )
    }
}
