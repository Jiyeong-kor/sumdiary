package com.jeong.sumdiary.domain.summary.usecase

import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import com.jeong.sumdiary.domain.summary.Summary
import com.jeong.sumdiary.domain.summary.SummaryGenerationResult
import com.jeong.sumdiary.domain.summary.SummaryRepository
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SummaryGenerationUseCaseTest {
    @Test
    fun dailySummaryUsesEntriesFromSelectedDate() = kotlinx.coroutines.test.runTest {
        val date = LocalDate(2026, 6, 5)
        val diaryRepository = FakeDiaryRepository(
            entry(id = "today", date = date, content = "오늘 기록"),
            entry(id = "other", date = LocalDate(2026, 6, 4), content = "어제 기록")
        )
        val summaryRepository = FakeSummaryRepository()
        val useCase = GenerateDailySummary(diaryRepository, summaryRepository)

        val result = assertIs<SummaryGenerationResult.Success>(useCase(date))

        assertEquals(SummaryType.DAILY, result.summary.type)
        assertEquals(date, result.periodStart)
        assertEquals(date, result.periodEnd)
        assertEquals(listOf("today"), summaryRepository.lastEntryIds)
    }

    @Test
    fun dailySummaryReturnsNoEntriesWhenSelectedDateHasNoEntries() = kotlinx.coroutines.test.runTest {
        val date = LocalDate(2026, 6, 5)
        val useCase = GenerateDailySummary(
            diaryRepository = FakeDiaryRepository(),
            summaryRepository = FakeSummaryRepository()
        )

        val result = assertIs<SummaryGenerationResult.NoEntries>(useCase(date))

        assertEquals(date, result.periodStart)
        assertEquals(date, result.periodEnd)
    }

    @Test
    fun dailySummaryReturnsUnsupportedWhenSummarizerIsUnavailable() = kotlinx.coroutines.test.runTest {
        val date = LocalDate(2026, 6, 5)
        val useCase = GenerateDailySummary(
            diaryRepository = FakeDiaryRepository(entry(id = "today", date = date)),
            summaryRepository = FakeSummaryRepository(summary = null)
        )

        val result = assertIs<SummaryGenerationResult.Unsupported>(useCase(date))

        assertEquals(date, result.periodStart)
        assertEquals(date, result.periodEnd)
    }

    @Test
    fun weeklySummaryUsesMondayToSundayPeriodForAnchorDate() = kotlinx.coroutines.test.runTest {
        val anchorDate = LocalDate(2026, 6, 5)
        val periodStart = LocalDate(2026, 6, 1)
        val periodEnd = LocalDate(2026, 6, 7)
        val diaryRepository = FakeDiaryRepository(
            entry(id = "mon", date = periodStart),
            entry(id = "fri", date = anchorDate),
            entry(id = "sun", date = periodEnd),
            entry(id = "next", date = LocalDate(2026, 6, 8))
        )
        val summaryRepository = FakeSummaryRepository()
        val useCase = GenerateWeeklySummary(diaryRepository, summaryRepository)

        val result = assertIs<SummaryGenerationResult.Success>(useCase(anchorDate))

        assertEquals(SummaryType.WEEKLY, result.summary.type)
        assertEquals(periodStart, result.periodStart)
        assertEquals(periodEnd, result.periodEnd)
        assertEquals(listOf("mon", "fri", "sun"), summaryRepository.lastEntryIds)
    }

    @Test
    fun weeklySummaryReturnsNoEntriesForEmptyWeek() = kotlinx.coroutines.test.runTest {
        val anchorDate = LocalDate(2026, 6, 5)
        val useCase = GenerateWeeklySummary(
            diaryRepository = FakeDiaryRepository(),
            summaryRepository = FakeSummaryRepository()
        )

        val result = assertIs<SummaryGenerationResult.NoEntries>(useCase(anchorDate))

        assertEquals(LocalDate(2026, 6, 1), result.periodStart)
        assertEquals(LocalDate(2026, 6, 7), result.periodEnd)
    }

    @Test
    fun weeklySummaryReturnsUnsupportedWhenSummarizerIsUnavailable() = kotlinx.coroutines.test.runTest {
        val anchorDate = LocalDate(2026, 6, 5)
        val useCase = GenerateWeeklySummary(
            diaryRepository = FakeDiaryRepository(entry(id = "fri", date = anchorDate)),
            summaryRepository = FakeSummaryRepository(summary = null)
        )

        val result = assertIs<SummaryGenerationResult.Unsupported>(useCase(anchorDate))

        assertEquals(LocalDate(2026, 6, 1), result.periodStart)
        assertEquals(LocalDate(2026, 6, 7), result.periodEnd)
    }

    private fun entry(
        id: String,
        date: LocalDate,
        content: String = "기록"
    ): DiaryEntry = DiaryEntry(
        id = id,
        date = date,
        time = LocalTime(9, 0),
        content = content
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
        var lastEntryIds: List<String> = emptyList()
            private set

        override suspend fun summarize(entries: List<DiaryEntry>): Summary? {
            lastEntryIds = entries.map { it.id }
            return summary
        }

        override suspend fun get(periodStart: LocalDate, periodEnd: LocalDate): Summary? = summary
    }
}
