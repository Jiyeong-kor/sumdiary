package com.jeong.sumdiary.feature.entry

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class EntryState(
    val text: String,
    val date: LocalDate,
    val time: LocalTime,
    val saving: Boolean
)
