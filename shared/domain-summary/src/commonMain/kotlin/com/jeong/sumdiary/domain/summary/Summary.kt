package com.jeong.sumdiary.domain.summary

import kotlinx.datetime.LocalDate

data class Summary(
    val type: SummaryType,
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val text: String,
    val emotions: List<String>,
)

enum class SummaryType {
    DAILY,
    WEEKLY,
    MONTHLY
}
