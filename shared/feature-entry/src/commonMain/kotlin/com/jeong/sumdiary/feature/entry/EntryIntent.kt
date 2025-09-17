package com.jeong.sumdiary.feature.entry

sealed interface EntryIntent {
    data class Edit(val text: String) : EntryIntent
    data object Save : EntryIntent
}
