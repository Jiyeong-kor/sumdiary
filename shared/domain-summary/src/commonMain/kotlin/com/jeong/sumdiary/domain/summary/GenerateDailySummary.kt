package com.jeong.sumdiary.domain.summary

import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.datetime.LocalDate

class GenerateDailySummary(
    private val diaryRepository: DiaryRepository,
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(date: LocalDate): Summary {
        val entries = diaryRepository.getByDate(date)
        return summaryRepository.summarize(entries)
    }
}
