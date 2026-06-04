package com.jeong.sumdiary.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

fun LocalDate.displayText(): String =
    "${year.toString().padStart(4, '0')}-${(month.ordinal + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"

fun LocalTime.displayText(): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

fun LocalDateTime.toPair(): Pair<LocalDate, LocalTime> = date to time
