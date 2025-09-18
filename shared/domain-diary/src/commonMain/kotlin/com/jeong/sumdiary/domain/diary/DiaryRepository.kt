package com.jeong.sumdiary.domain.diary

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface DiaryRepository {
    suspend fun upsert(entry: DiaryEntry)
    suspend fun getByDate(date: LocalDate): List<DiaryEntry>
    fun observeRange(from: LocalDate, to: LocalDate): Flow<List<DiaryEntry>>
}
