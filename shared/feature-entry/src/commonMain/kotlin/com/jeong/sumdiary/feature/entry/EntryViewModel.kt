package com.jeong.sumdiary.feature.entry

import com.jeong.sumdiary.core.util.DefaultDispatcherProvider
import com.jeong.sumdiary.core.util.DispatcherProvider
import com.jeong.sumdiary.core.util.IdGenerator
import com.jeong.sumdiary.core.util.Logger
import com.jeong.sumdiary.domain.diary.model.DiaryEntry
import com.jeong.sumdiary.domain.diary.repository.DiaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class EntryViewModel(
    private val diaryRepository: DiaryRepository,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider
) {
    private val scope = CoroutineScope(dispatcherProvider.default + SupervisorJob())
    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<EntryState> = _state.asStateFlow()

    fun handle(intent: EntryIntent) {
        when (intent) {
            is EntryIntent.Edit -> _state.update { it.copy(text = intent.text) }
            EntryIntent.Save -> saveEntry()
        }
    }

    fun dispose() {
        scope.cancel()
    }

    private fun createInitialState(): EntryState {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return EntryState(
            text = "",
            date = now.date,
            time = now.time,
            saving = false,
        )
    }

    private fun saveEntry() {
        val current = _state.value
        if (current.saving || current.text.isBlank()) {
            return
        }
        _state.update { it.copy(saving = true) }
        scope.launch(dispatcherProvider.io) {
            runCatching {
                diaryRepository.upsert(
                    DiaryEntry(
                        id = IdGenerator.create(),
                        date = current.date,
                        time = current.time,
                        content = current.text,
                    ),
                )
            }.onFailure { error ->
                Logger.e("일기 저장 실패", error)
            }
            _state.update {
                it.copy(
                    text = "",
                    saving = false,
                )
            }
        }
    }
}
