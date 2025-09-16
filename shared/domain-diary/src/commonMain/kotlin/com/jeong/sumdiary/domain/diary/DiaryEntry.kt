package com.jeong.sumdiary.domain.diary

import com.jeong.sumdiary.core.model.DiaryEntryId
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class DiaryEntry(
    val id: DiaryEntryId,
    val date: LocalDate,
    val time: LocalTime,
    val content: String
)
