package com.jeong.sumdiary.feature.entry

import com.jeong.sumdiary.core.model.DiaryEntryId
import com.jeong.sumdiary.core.util.DateUtils
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class EntryViewModel(
    private val diaryRepository: DiaryRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(defaultState())
    val state: StateFlow<EntryState> = _state.asStateFlow()

    fun dispatch(intent: EntryIntent) {
        when (intent) {
            is EntryIntent.Edit -> _state.value = _state.value.copy(text = intent.text)
            is EntryIntent.UpdateDate -> _state.value = _state.value.copy(date = intent.date)
            is EntryIntent.UpdateTime -> _state.value = _state.value.copy(time = intent.time)
            EntryIntent.Save -> saveEntry()
        }
    }

    private fun saveEntry() {
        val current = _state.value
        if (current.text.isBlank()) return
        _state.value = current.copy(saving = true)
        scope.launch {
            val entry = DiaryEntry(
                id = DiaryEntryId(generateId(current.date, current.time)),
                date = current.date,
                time = current.time,
                content = current.text,
            )
            diaryRepository.upsert(entry)
            _state.value = defaultState()
        }
    }

    fun clear() {
        scope.cancel()
    }

    private fun defaultState(): EntryState = EntryState(
        text = "",
        date = DateUtils.nowDate(),
        time = DateUtils.nowTime(),
        saving = false,
    )

    private fun generateId(date: LocalDate, time: LocalTime): String {
        return buildString {
            append(date.toString())
            append('-')
            append(time.toString())
            append('-')
            append(DateUtils.nowTime().toString())
        }
    }
}
