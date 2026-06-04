package com.jeong.sumdiary.domain.summary

import kotlinx.datetime.LocalDate

sealed interface SummaryGenerationResult {
    val periodStart: LocalDate
    val periodEnd: LocalDate

    data class Success(
        val summary: Summary
    ) : SummaryGenerationResult {
        override val periodStart: LocalDate = summary.periodStart
        override val periodEnd: LocalDate = summary.periodEnd
    }

    data class NoEntries(
        override val periodStart: LocalDate,
        override val periodEnd: LocalDate
    ) : SummaryGenerationResult

    data class Unsupported(
        override val periodStart: LocalDate,
        override val periodEnd: LocalDate
    ) : SummaryGenerationResult
}
