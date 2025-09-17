package com.jeong.sumdiary.feature.summary

import com.jeong.sumdiary.core.util.DefaultDispatcherProvider
import com.jeong.sumdiary.core.util.DispatcherProvider
import com.jeong.sumdiary.core.util.Logger
import com.jeong.sumdiary.domain.summary.usecase.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.usecase.GenerateWeeklySummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class SummaryViewModel(
    private val generateDailySummary: GenerateDailySummary,
    private val generateWeeklySummary: GenerateWeeklySummary,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider
) {
    private val scope = CoroutineScope(dispatcherProvider.default + SupervisorJob())
    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<SummaryState> = _state.asStateFlow()

    fun handle(intent: SummaryIntent) {
        when (intent) {
            is SummaryIntent.LoadDaily -> loadDaily(intent.date)
            is SummaryIntent.LoadWeekly -> loadWeekly(intent.weekStart)
        }
    }

    fun dispose() {
        scope.cancel()
    }

    private fun createInitialState(): SummaryState {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return SummaryState(
            period = today to today,
            text = "요약을 불러오는 중입니다.",
            emotions = emptyList(),
            loading = false
        )
    }

    private fun loadDaily(date: LocalDate) {
        _state.update { it.copy(loading = true, period = date to date) }
        scope.launch(dispatcherProvider.io) {
            runCatching {
                generateDailySummary(date)
            }.onSuccess { summary ->
                _state.update {
                    it.copy(
                        text = summary.text,
                        emotions = summary.emotions,
                        loading = false
                    )
                }
            }.onFailure { error ->
                Logger.e("일일 요약 로딩 실패", error)
                _state.update { it.copy(text = "요약을 불러오지 못했습니다.", loading = false) }
            }
        }
    }

    private fun loadWeekly(weekStart: LocalDate) {
        val weekEnd = weekStart + DatePeriod(days = 6)
        _state.update { it.copy(loading = true, period = weekStart to weekEnd) }
        scope.launch(dispatcherProvider.io) {
            runCatching {
                generateWeeklySummary(weekStart)
            }.onSuccess { summary ->
                _state.update {
                    it.copy(
                        text = summary.text,
                        emotions = summary.emotions,
                        loading = false
                    )
                }
            }.onFailure { error ->
                Logger.e("주간 요약 로딩 실패", error)
                _state.update { it.copy(text = "요약을 불러오지 못했습니다.", loading = false) }
            }
        }
    }
}
