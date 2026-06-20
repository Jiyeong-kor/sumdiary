package com.jeong.sumdiary.feature.entry

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class EntryState(
    val text: String,
    val date: LocalDate,
    val time: LocalTime,
    val saving: Boolean
) {
    companion object {
        @OptIn(ExperimentalTime::class)
        fun initial(): EntryState {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return EntryState(
                text = "",
                date = now.date,
                time = now.time,
                saving = false
            )
        }
    }
}
