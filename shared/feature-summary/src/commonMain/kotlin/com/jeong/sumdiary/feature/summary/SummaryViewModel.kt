package com.jeong.sumdiary.feature.summary

import com.jeong.sumdiary.domain.summary.SummaryGenerationResult
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
            _state.value = SummaryState(
                period = date to date,
                text = "",
                emotions = emptyList(),
                status = SummaryUiStatus.LOADING
            )
            runCatching { generateDailySummary(date) }
                .onSuccess { result -> _state.value = result.toState() }
                .onFailure { _state.value = _state.value.toFailedState() }
        }
    }

    private fun loadWeekly(anchor: LocalDate) {
        scope.launch {
            _state.value = _state.value.copy(
                text = "",
                emotions = emptyList(),
                status = SummaryUiStatus.LOADING
            )
            runCatching { generateWeeklySummary(anchor) }
                .onSuccess { result -> _state.value = result.toState() }
                .onFailure { _state.value = _state.value.toFailedState() }
        }
    }

    private fun SummaryGenerationResult.toState(): SummaryState =
        when (this) {
            is SummaryGenerationResult.Success -> SummaryState(
                period = periodStart to periodEnd,
                text = summary.text,
                emotions = summary.emotions,
                status = SummaryUiStatus.CONTENT
            )
            is SummaryGenerationResult.NoEntries -> SummaryState(
                period = periodStart to periodEnd,
                text = "",
                emotions = emptyList(),
                status = SummaryUiStatus.NO_ENTRIES
            )
            is SummaryGenerationResult.Unsupported -> SummaryState(
                period = periodStart to periodEnd,
                text = "",
                emotions = emptyList(),
                status = SummaryUiStatus.UNSUPPORTED
            )
        }

    private fun SummaryState.toFailedState(): SummaryState =
        copy(
            text = "",
            emotions = emptyList(),
            status = SummaryUiStatus.FAILED
        )
}
