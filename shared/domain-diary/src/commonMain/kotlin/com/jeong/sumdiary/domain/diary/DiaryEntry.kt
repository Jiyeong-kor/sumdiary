package com.jeong.sumdiary.domain.diary

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class DiaryEntry(
    val id: String,
    val date: LocalDate,
    val time: LocalTime,
    val content: String
)
