package com.jeong.sumdiary.domain.summary

import com.jeong.sumdiary.domain.diary.DiaryEntry
import kotlinx.datetime.LocalDate

interface SummaryRepository {
    suspend fun summarize(entries: List<DiaryEntry>): Summary

    suspend fun get(periodStart: LocalDate, periodEnd: LocalDate): Summary?
}
