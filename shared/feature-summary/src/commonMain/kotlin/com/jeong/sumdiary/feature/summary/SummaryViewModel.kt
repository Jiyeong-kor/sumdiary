package com.jeong.sumdiary.feature.summary

import com.jeong.sumdiary.domain.summary.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.GenerateWeeklySummary
import com.jeong.sumdiary.domain.summary.Summary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class SummaryViewModel(
    private val generateDailySummary: GenerateDailySummary,
    private val generateWeeklySummary: GenerateWeeklySummary,
    private val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _state = MutableStateFlow(
        SummaryState.idle(today = LocalDate(1970, 1, 1)),
    )
    val state: StateFlow<SummaryState> = _state

    fun onIntent(intent: SummaryIntent) {
        when (intent) {
            is SummaryIntent.LoadDaily -> loadDaily(intent.date)
            is SummaryIntent.LoadWeekly -> loadWeekly(intent.weekStart)
        }
    }

    private fun loadDaily(date: LocalDate) {
        _state.value = _state.value.copy(period = date to date, loading = true)
        scope.launch {
            val result = runCatching { generateDailySummary(date) }
            applySummary(result.getOrNull())
        }
    }

    private fun loadWeekly(weekStart: LocalDate) {
        val weekEnd = weekStart.plus(DatePeriod(days = 6))
        _state.value = _state.value.copy(period = weekStart to weekEnd, loading = true)
        scope.launch {
            val result = runCatching { generateWeeklySummary(weekStart) }
            applySummary(result.getOrNull())
        }
    }


    private fun applySummary(summary: Summary?) {
        if (summary == null) {
            _state.value = _state.value.copy(text = "", emotions = emptyList(), loading = false)
        } else {
            _state.value = _state.value.copy(
                period = summary.periodStart to summary.periodEnd,
                text = summary.text,
                emotions = summary.emotions,
                loading = false
            )
        }
    }

    fun clear() {
        scope.cancel()
    }
}
