package com.jeong.sumdiary.feature.summary

import com.jeong.sumdiary.core.util.DateUtils
import com.jeong.sumdiary.domain.summary.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.GenerateWeeklySummary
import com.jeong.sumdiary.domain.summary.Summary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class SummaryViewModel(
    private val generateDailySummary: GenerateDailySummary,
    private val generateWeeklySummary: GenerateWeeklySummary,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(defaultState())
    val state: StateFlow<SummaryState> = _state.asStateFlow()

    fun dispatch(intent: SummaryIntent) {
        when (intent) {
            is SummaryIntent.LoadDaily -> loadDaily(intent.date)
            is SummaryIntent.LoadWeekly -> loadWeekly(intent.periodStart)
        }
    }

    private fun loadDaily(date: LocalDate) {
        _state.value = _state.value.copy(loading = true)
        scope.launch {
            val summary = generateDailySummary(date)
            _state.value = summary.toState()
        }
    }

    private fun loadWeekly(start: LocalDate) {
        _state.value = _state.value.copy(loading = true)
        scope.launch {
            val summary = generateWeeklySummary(start)
            _state.value = summary.toState()
        }
    }

    fun clear() {
        scope.cancel()
    }

    private fun Summary.toState(): SummaryState = SummaryState(
        period = periodStart to periodEnd,
        text = text,
        emotions = emotions,
        loading = false,
    )

    private fun defaultState(): SummaryState {
        val today = DateUtils.nowDate()
        return SummaryState(
            period = today to today,
            text = "요약을 불러오세요.",
            emotions = emptyList(),
            loading = false,
        )
    }
}
