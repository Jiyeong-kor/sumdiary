package com.jeong.sumdiary.feature.summary

import kotlinx.datetime.LocalDate

enum class SummaryUiStatus {
    NOT_GENERATED,
    LOADING,
    CONTENT,
    NO_ENTRIES,
    UNSUPPORTED,
    FAILED
}

data class SummaryState(
    val period: Pair<LocalDate, LocalDate>,
    val text: String,
    val emotions: List<String>,
    val status: SummaryUiStatus
) {
    val loading: Boolean
        get() = status == SummaryUiStatus.LOADING

    companion object {
        fun initial(date: LocalDate): SummaryState = SummaryState(
            period = date to date,
            text = "",
            emotions = emptyList(),
            status = SummaryUiStatus.NOT_GENERATED
        )
    }
}
