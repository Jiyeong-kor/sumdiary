package com.jeong.sumdiary.feature.entry

import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EntryViewModel(
    private val repository: DiaryRepository,
    private val dispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _state = MutableStateFlow(EntryState.initial())
    val state: StateFlow<EntryState> = _state.asStateFlow()

    fun dispatch(intent: EntryIntent) {
        when (intent) {
            is EntryIntent.EditText -> _state.value = _state.value.copy(text = intent.text)
            is EntryIntent.ChangeDate -> _state.value = _state.value.copy(date = intent.date)
            is EntryIntent.ChangeTime -> _state.value = _state.value.copy(time = intent.time)
            EntryIntent.Save -> save()
        }
    }

    private fun save() {
        val current = _state.value
        if (current.text.isBlank()) return
        scope.launch {
            _state.value = current.copy(saving = true)
            repository.upsert(
                DiaryEntry(
                    id = generateEntryId(current),
                    date = current.date,
                    time = current.time,
                    content = current.text
                )
            )
            _state.value = current.copy(saving = false, text = "")
        }
    }

    private fun generateEntryId(state: EntryState): String = buildString {
        append(state.date.toString())
        append('-')
        append(state.time.toString())
    }
}
