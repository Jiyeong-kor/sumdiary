package com.jeong.sumdiary.feature.entry

import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class EntryViewModel(
    private val repository: DiaryRepository,
    private val dispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _state = MutableStateFlow(EntryState.initial())
    private var observeEntriesJob: Job? = null
    val state: StateFlow<EntryState> = _state.asStateFlow()

    init {
        observeEntriesFor(_state.value.date)
    }

    fun dispatch(intent: EntryIntent) {
        when (intent) {
            is EntryIntent.EditText -> _state.value = _state.value.copy(text = intent.text)
            is EntryIntent.ChangeDate -> {
                _state.value = _state.value.copy(date = intent.date)
                observeEntriesFor(intent.date)
            }
            is EntryIntent.ChangeTime -> _state.value = _state.value.copy(time = intent.time)
            is EntryIntent.StartEdit -> startEdit(intent.entry)
            is EntryIntent.Delete -> delete(intent.id)
            EntryIntent.CancelEdit -> clearEditor()
            EntryIntent.Save -> save()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun save() {
        val current = _state.value
        if (current.text.isBlank()) return
        scope.launch {
            _state.value = _state.value.copy(saving = true)
            repository.upsert(
                DiaryEntry(
                    id = current.editingEntryId ?: generateEntryId(current),
                    date = current.date,
                    time = current.time,
                    content = current.text
                )
            )
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            _state.value = _state.value.copy(
                text = "",
                time = now.time,
                editingEntryId = null,
                saving = false
            )
        }
    }

    private fun startEdit(entry: DiaryEntry) {
        _state.value = _state.value.copy(
            text = entry.content,
            date = entry.date,
            time = entry.time,
            editingEntryId = entry.id
        )
    }

    private fun delete(id: String) {
        scope.launch {
            repository.deleteById(id)
            if (_state.value.editingEntryId == id) {
                clearEditor()
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun clearEditor() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        _state.value = _state.value.copy(
            text = "",
            date = now.date,
            time = now.time,
            editingEntryId = null,
            saving = false
        )
    }

    private fun generateEntryId(state: EntryState): String = buildString {
        append(state.date.toString())
        append('-')
        append(state.time.toString())
    }

    private fun observeEntriesFor(date: LocalDate) {
        observeEntriesJob?.cancel()
        observeEntriesJob = scope.launch {
            repository.observeRange(date, date)
                .catch { _state.value = _state.value.copy(entries = emptyList()) }
                .collect { entries ->
                    _state.value = _state.value.copy(entries = entries)
                }
        }
    }
}
