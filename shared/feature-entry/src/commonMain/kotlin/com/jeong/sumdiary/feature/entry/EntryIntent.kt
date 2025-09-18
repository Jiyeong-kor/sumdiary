package com.jeong.sumdiary.feature.entry

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed interface EntryIntent {
    data class EditText(val text: String) : EntryIntent
    data class ChangeDate(val date: LocalDate) : EntryIntent
    data class ChangeTime(val time: LocalTime) : EntryIntent
    object Save : EntryIntent
}
