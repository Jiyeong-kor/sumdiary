package com.jeong.sumdiary.core.util.time

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object TimeProvider {
    fun today(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
        return now(timeZone).date
    }

    @OptIn(ExperimentalTime::class)
    fun now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
        return Clock.System.now().toLocalDateTime(timeZone)
    }
}
