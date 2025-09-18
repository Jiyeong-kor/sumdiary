package com.jeong.sumdiary.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.dayOfMonth
import kotlinx.datetime.monthNumber

fun LocalDate.displayText(): String = "%04d-%02d-%02d".format(year, monthNumber, dayOfMonth)

fun LocalTime.displayText(): String = "%02d:%02d".format(hour, minute)

fun LocalDateTime.toPair(): Pair<LocalDate, LocalTime> = date to time
