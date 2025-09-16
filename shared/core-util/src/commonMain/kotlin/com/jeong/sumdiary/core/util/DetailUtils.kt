package com.jeong.sumdiary.core.util

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateUtils {
    fun nowDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
        return Clock.System.now().toLocalDateTime(timeZone).date
    }

    fun nowTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime {
        return Clock.System.now().toLocalDateTime(timeZone).time
    }

    fun combine(date: LocalDate, time: LocalTime): LocalDateTime =
        LocalDateTime(
            date.year,
            date.monthNumber,
            date.dayOfMonth,
            time.hour,
            time.minute,
            time.second
        )
}
