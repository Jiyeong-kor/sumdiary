package com.jeong.sumdiary.feature.entry

import com.jeong.sumdiary.core.util.time.TimeProvider
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

class EntryViewModel(
    private val diaryRepository: DiaryRepository,
    private val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _state = MutableStateFlow(
        EntryState.initial(
            date = TimeProvider.today(TimeZone.currentSystemDefault()),
            time = TimeProvider.now(TimeZone.currentSystemDefault()).time,
        ),
    )
    val state: StateFlow<EntryState> = _state
    fun onIntent(intent: EntryIntent) {
        when (intent) {
            is EntryIntent.Edit -> onEdit(intent)
            EntryIntent.Save -> onSave()
        }
    }

    private fun onEdit(intent: EntryIntent.Edit) {
        _state.value = EntryState(
            text = intent.text,
            date = intent.date,
            time = intent.time,
            saving = false
        )
    }

    private fun onSave() {
        val current = _state.value
        if (current.saving) return
        _state.value = current.copy(saving = true)
        scope.launch {
            val entry = DiaryEntry(
                id = generateEntryId(current.date, current.time),
                date = current.date,
                time = current.time,
                content = current.text,
            )
            runCatching { diaryRepository.upsert(entry) }
            _state.value = _state.value.copy(saving = false)
        }
    }

    private fun generateEntryId(date: LocalDate, time: LocalTime): String {
        return "${'$'}date-${'$'}time-${Random.nextInt(0, Int.MAX_VALUE)}"
    }

    fun clear() {
        scope.cancel()
    }
}
