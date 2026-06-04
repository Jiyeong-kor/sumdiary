package com.jeong.sumdiary.feature.entry

import com.jeong.sumdiary.domain.diary.DiaryEntry
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed interface EntryIntent {
    data class EditText(val text: String) : EntryIntent
    data class ChangeDate(val date: LocalDate) : EntryIntent
    data class ChangeTime(val time: LocalTime) : EntryIntent
    data class StartEdit(val entry: DiaryEntry) : EntryIntent
    data class Delete(val id: String) : EntryIntent
    object CancelEdit : EntryIntent
    object Save : EntryIntent
}
