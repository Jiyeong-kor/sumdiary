package com.jeong.sumdiary.domain.summary.usecase

import com.jeong.sumdiary.domain.diary.DiaryRepository
import com.jeong.sumdiary.domain.summary.SummaryGenerationResult
import com.jeong.sumdiary.domain.summary.SummaryRepository
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.datetime.LocalDate

class GenerateDailySummary(
    private val diaryRepository: DiaryRepository,
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(date: LocalDate): SummaryGenerationResult {
        val entries = diaryRepository.getByDate(date)
        if (entries.isEmpty()) {
            return SummaryGenerationResult.NoEntries(
                periodStart = date,
                periodEnd = date
            )
        }
        val summary = summaryRepository.summarize(entries)
        return SummaryGenerationResult.Success(
            summary.copy(
                type = SummaryType.DAILY,
                periodStart = date,
                periodEnd = date
            )
        )
    }
}
