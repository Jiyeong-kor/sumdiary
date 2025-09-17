package com.jeong.sumdiary.domain.summary.repository

import com.jeong.sumdiary.domain.diary.model.DiaryEntry
import com.jeong.sumdiary.domain.summary.model.Summary
import kotlinx.datetime.LocalDate

interface SummaryRepository {
    suspend fun summarize(entries: List<DiaryEntry>): Summary

    suspend fun get(periodStart: LocalDate, periodEnd: LocalDate): Summary?
}
