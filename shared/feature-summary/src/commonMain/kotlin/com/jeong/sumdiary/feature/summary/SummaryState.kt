package com.jeong.sumdiary.feature.summary

import kotlinx.datetime.LocalDate

data class SummaryState(
    val period: Pair<LocalDate, LocalDate>,
    val text: String,
    val emotions: List<String>,
    val loading: Boolean
) {
    companion object {
        fun idle(today: LocalDate): SummaryState = SummaryState(
            period = today to today,
            text = "",
            emotions = emptyList(),
            loading = false,
        )
    }
}
