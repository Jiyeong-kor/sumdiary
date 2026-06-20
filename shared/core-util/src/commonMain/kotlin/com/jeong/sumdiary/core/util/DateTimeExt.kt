package com.jeong.sumdiary.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Suppress("DEPRECATION")
fun LocalDate.displayText(): String = "${year.pad(4)}-${monthNumber.pad()}-${day.pad()}"

fun LocalTime.displayText(): String = "${hour.pad()}:${minute.pad()}"

fun LocalDateTime.toPair(): Pair<LocalDate, LocalTime> = date to time

private fun Int.pad(size: Int = 2): String = toString().padStart(size, '0')
