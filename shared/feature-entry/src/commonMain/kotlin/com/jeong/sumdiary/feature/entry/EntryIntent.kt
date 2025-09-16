package com.jeong.sumdiary.feature.entry

sealed interface EntryIntent {
    data class Edit(val text: String) : EntryIntent
    data class UpdateDate(val date: kotlinx.datetime.LocalDate) : EntryIntent
    data class UpdateTime(val time: kotlinx.datetime.LocalTime) : EntryIntent
    data object Save : EntryIntent
}
