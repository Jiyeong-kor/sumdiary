package com.jeong.sumdiary.domain.summary.usecase

import com.jeong.sumdiary.domain.diary.repository.DiaryRepository
import com.jeong.sumdiary.domain.summary.model.Summary
import com.jeong.sumdiary.domain.summary.model.SummaryType
import com.jeong.sumdiary.domain.summary.repository.SummaryRepository
import kotlinx.datetime.LocalDate

class GenerateDailySummary(
    private val diaryRepository: DiaryRepository,
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(date: LocalDate): Summary {
        val entries = diaryRepository.getByDate(date)
        val baseSummary = summaryRepository.summarize(entries)
        return baseSummary.copy(
            type = SummaryType.DAILY,
            periodStart = date,
            periodEnd = date
        )
    }
}
