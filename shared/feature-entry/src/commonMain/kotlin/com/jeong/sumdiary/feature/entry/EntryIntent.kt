package com.jeong.sumdiary.feature.entry

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed interface EntryIntent {
    data class Edit(
        val text: String,
        val date: LocalDate,
        val time: LocalTime
    ) : EntryIntent

    data object Save : EntryIntent
}
