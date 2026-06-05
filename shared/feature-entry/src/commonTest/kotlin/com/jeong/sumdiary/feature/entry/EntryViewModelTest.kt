package com.jeong.sumdiary.feature.entry

import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EntryViewModelTest {
    @Test
    fun saveCreatesEntryForSelectedDateAndClearsEditor() {
        val repository = FakeDiaryRepository()
        val viewModel = viewModel(repository)
        val date = LocalDate(2026, 6, 5)
        val time = LocalTime(21, 10)

        viewModel.dispatch(EntryIntent.ChangeDate(date))
        viewModel.dispatch(EntryIntent.ChangeTime(time))
        viewModel.dispatch(EntryIntent.EditText("오늘의 첫 기록"))
        viewModel.dispatch(EntryIntent.Save)

        val saved = repository.entries.single()
        assertEquals("2026-06-05-21:10", saved.id)
        assertEquals(date, saved.date)
        assertEquals(time, saved.time)
        assertEquals("오늘의 첫 기록", saved.content)
        assertEquals("", viewModel.state.value.text)
        assertNull(viewModel.state.value.editingEntryId)
        assertTrue(viewModel.state.value.entries.any { it.id == saved.id })
    }

    @Test
    fun saveUpdatesEditingEntryWithoutChangingItsId() {
        val existing = DiaryEntry(
            id = "entry-1",
            date = LocalDate(2026, 6, 5),
            time = LocalTime(8, 30),
            content = "수정 전"
        )
        val repository = FakeDiaryRepository(existing)
        val viewModel = viewModel(repository)

        viewModel.dispatch(EntryIntent.StartEdit(existing))
        viewModel.dispatch(EntryIntent.EditText("수정 후"))
        viewModel.dispatch(EntryIntent.ChangeTime(LocalTime(9, 0)))
        viewModel.dispatch(EntryIntent.Save)

        val saved = repository.entries.single()
        assertEquals("entry-1", saved.id)
        assertEquals("수정 후", saved.content)
        assertEquals(LocalTime(9, 0), saved.time)
        assertEquals("", viewModel.state.value.text)
        assertNull(viewModel.state.value.editingEntryId)
    }

    @Test
    fun deleteRemovesEntryAndClearsEditorWhenEditingSameEntry() {
        val existing = DiaryEntry(
            id = "entry-1",
            date = LocalDate(2026, 6, 5),
            time = LocalTime(8, 30),
            content = "삭제할 기록"
        )
        val repository = FakeDiaryRepository(existing)
        val viewModel = viewModel(repository)

        viewModel.dispatch(EntryIntent.StartEdit(existing))
        viewModel.dispatch(EntryIntent.Delete(existing.id))

        assertTrue(repository.entries.isEmpty())
        assertEquals("", viewModel.state.value.text)
        assertNull(viewModel.state.value.editingEntryId)
        assertTrue(viewModel.state.value.entries.none { it.id == existing.id })
    }

    @Test
    fun saveIgnoresBlankText() {
        val repository = FakeDiaryRepository()
        val viewModel = viewModel(repository)

        viewModel.dispatch(EntryIntent.EditText("   "))
        viewModel.dispatch(EntryIntent.Save)

        assertTrue(repository.entries.isEmpty())
    }

    private fun viewModel(
        repository: FakeDiaryRepository,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
    ): EntryViewModel = EntryViewModel(
        repository = repository,
        dispatcher = dispatcher
    )

    private class FakeDiaryRepository(
        initialEntry: DiaryEntry? = null
    ) : DiaryRepository {
        private val entriesFlow = MutableStateFlow(initialEntry?.let(::listOf) ?: emptyList())

        val entries: List<DiaryEntry>
            get() = entriesFlow.value

        override suspend fun upsert(entry: DiaryEntry) {
            entriesFlow.value = entriesFlow.value
                .filterNot { it.id == entry.id } + entry
        }

        override suspend fun deleteById(id: String) {
            entriesFlow.value = entriesFlow.value.filterNot { it.id == id }
        }

        override suspend fun getByDate(date: LocalDate): List<DiaryEntry> =
            entriesFlow.value.filter { it.date == date }

        override fun observeRange(from: LocalDate, to: LocalDate): Flow<List<DiaryEntry>> =
            entriesFlow.map { entries ->
                entries.filter { entry -> entry.date in from..to }
            }
    }
}
