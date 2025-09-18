package com.jeong.sumdiary.feature.summary

import com.jeong.sumdiary.domain.summary.usecase.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.usecase.GenerateWeeklySummary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class SummaryViewModel(
    private val generateDailySummary: GenerateDailySummary,
    private val generateWeeklySummary: GenerateWeeklySummary,
    private val dispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _state = MutableStateFlow(SummaryState.initial(LocalDate(1970, 1, 1)))
    val state: StateFlow<SummaryState> = _state.asStateFlow()

    fun dispatch(intent: SummaryIntent) {
        when (intent) {
            is SummaryIntent.LoadDaily -> loadDaily(intent.date)
            is SummaryIntent.LoadWeekly -> loadWeekly(intent.anchorDate)
        }
    }

    private fun loadDaily(date: LocalDate) {
        scope.launch {
            _state.value = _state.value.copy(loading = true)
            val summary = generateDailySummary(date)
            _state.value = SummaryState(
                period = summary.periodStart to summary.periodEnd,
                text = summary.text,
                emotions = summary.emotions,
                loading = false
            )
        }
    }

    private fun loadWeekly(anchor: LocalDate) {
        scope.launch {
            _state.value = _state.value.copy(loading = true)
            val summary = generateWeeklySummary(anchor)
            _state.value = SummaryState(
                period = summary.periodStart to summary.periodEnd,
                text = summary.text,
                emotions = summary.emotions,
                loading = false
            )
        }
    }
}
