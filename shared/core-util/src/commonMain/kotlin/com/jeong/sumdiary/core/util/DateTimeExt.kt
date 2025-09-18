package com.jeong.sumdiary.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.number

fun LocalDate.displayText(): String = "$year-%02d-%02d".format(month.number, day)

fun LocalTime.displayText(): String = "%02d:%02d".format(hour, minute)

fun LocalDateTime.toPair(): Pair<LocalDate, LocalTime> = date to time
