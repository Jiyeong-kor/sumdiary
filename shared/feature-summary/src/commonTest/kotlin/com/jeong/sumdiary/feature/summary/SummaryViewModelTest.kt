package com.jeong.sumdiary.feature.summary

import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import com.jeong.sumdiary.domain.summary.Summary
import com.jeong.sumdiary.domain.summary.SummaryRepository
import com.jeong.sumdiary.domain.summary.SummaryType
import com.jeong.sumdiary.domain.summary.usecase.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.usecase.GenerateWeeklySummary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class SummaryViewModelTest {
    @Test
    fun loadDailyMapsGeneratedSummaryToContentState() {
        val date = LocalDate(2026, 6, 5)
        val viewModel = viewModel(
            diaryRepository = FakeDiaryRepository(entry(id = "today", date = date)),
            summaryRepository = FakeSummaryRepository()
        )

        viewModel.dispatch(SummaryIntent.LoadDaily(date))

        val state = viewModel.state.value
        assertEquals(SummaryUiStatus.CONTENT, state.status)
        assertEquals(date to date, state.period)
        assertEquals("요약", state.text)
        assertEquals(listOf("calm"), state.emotions)
    }

    @Test
    fun loadWeeklyMapsUnsupportedResultToUnsupportedState() {
        val anchorDate = LocalDate(2026, 6, 5)
        val viewModel = viewModel(
            diaryRepository = FakeDiaryRepository(entry(id = "fri", date = anchorDate)),
            summaryRepository = FakeSummaryRepository(summary = null)
        )

        viewModel.dispatch(SummaryIntent.LoadWeekly(anchorDate))

        val state = viewModel.state.value
        assertEquals(SummaryUiStatus.UNSUPPORTED, state.status)
        assertEquals(LocalDate(2026, 6, 1) to LocalDate(2026, 6, 7), state.period)
        assertEquals("", state.text)
        assertEquals(emptyList(), state.emotions)
    }

    private fun viewModel(
        diaryRepository: FakeDiaryRepository,
        summaryRepository: FakeSummaryRepository,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
    ): SummaryViewModel = SummaryViewModel(
        generateDailySummary = GenerateDailySummary(diaryRepository, summaryRepository),
        generateWeeklySummary = GenerateWeeklySummary(diaryRepository, summaryRepository),
        dispatcher = dispatcher
    )

    private fun entry(id: String, date: LocalDate): DiaryEntry = DiaryEntry(
        id = id,
        date = date,
        time = LocalTime(9, 0),
        content = "기록"
    )

    private class FakeDiaryRepository(
        vararg initialEntries: DiaryEntry
    ) : DiaryRepository {
        private val entriesFlow = MutableStateFlow(initialEntries.toList())

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

    private class FakeSummaryRepository(
        private val summary: Summary? = Summary(
            type = SummaryType.DAILY,
            periodStart = LocalDate(1970, 1, 1),
            periodEnd = LocalDate(1970, 1, 1),
            text = "요약",
            emotions = listOf("calm")
        )
    ) : SummaryRepository {
        override suspend fun summarize(entries: List<DiaryEntry>): Summary? = summary

        override suspend fun get(periodStart: LocalDate, periodEnd: LocalDate): Summary? = summary
    }
}
