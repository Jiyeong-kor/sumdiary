package com.jeong.sumdiary.feature.summary

import kotlinx.datetime.LocalDate

sealed interface SummaryIntent {
    data class LoadDaily(val date: LocalDate) : SummaryIntent
    data class LoadWeekly(val weekStart: LocalDate) : SummaryIntent
}
