package com.jeong.sumdiary.feature.entry

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class EntryState(
    val text: String,
    val date: LocalDate,
    val time: LocalTime,
    val saving: Boolean
) {
    companion object {
        fun initial(date: LocalDate, time: LocalTime): EntryState = EntryState(
            text = "",
            date = date,
            time = time,
            saving = false,
        )
    }
}
