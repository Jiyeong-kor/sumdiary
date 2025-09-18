package com.jeong.sumdiary.feature.summary

import kotlinx.datetime.LocalDate

data class SummaryState(
    val period: Pair<LocalDate, LocalDate>,
    val text: String,
    val emotions: List<String>,
    val loading: Boolean
) {
    companion object {
        fun initial(date: LocalDate): SummaryState = SummaryState(
            period = date to date,
            text = "요약을 불러오세요.",
            emotions = emptyList(),
            loading = false
        )
    }
}
