package com.jeong.sumdiary.ios

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.jeong.sumdiary.core.util.DefaultDispatchersProvider
import com.jeong.sumdiary.data.diary.DiaryRepositoryImpl
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.summary.PlaceholderSummarizerEngine
import com.jeong.sumdiary.data.summary.SummaryRepositoryImpl
import com.jeong.sumdiary.data.summary.db.SummaryDatabase
import com.jeong.sumdiary.domain.summary.usecase.GenerateDailySummary
import com.jeong.sumdiary.feature.entry.EntryIntent
import com.jeong.sumdiary.feature.entry.EntryViewModel
import com.jeong.sumdiary.feature.summary.SummaryIntent
import com.jeong.sumdiary.feature.summary.SummaryViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class IosAppFactory {
    private val dispatchers = DefaultDispatchersProvider
    private val diaryDatabase = DiaryDatabase(
        NativeSqliteDriver(DiaryDatabase.Schema, "diary.db")
    )
    private val summaryDatabase = SummaryDatabase(
        NativeSqliteDriver(SummaryDatabase.Schema, "summary.db")
    )

    private val diaryRepository = DiaryRepositoryImpl(diaryDatabase, dispatchers.io)
    private val summaryRepository = SummaryRepositoryImpl(
        summaryDatabase,
        dispatchers.io,
        PlaceholderSummarizerEngine()
    )

    private val generateDailySummary = GenerateDailySummary(diaryRepository, summaryRepository)
    private val generateWeeklySummary = GenerateWeeklySummary(diaryRepository, summaryRepository)

    fun createSummaryViewModel(): SummaryViewModel = SummaryViewModel(
        generateDailySummary,
        generateWeeklySummary,
        dispatchers.io
    )

    fun createEntryViewModel(): EntryViewModel = EntryViewModel(
        diaryRepository,
        dispatchers.io
    )
}

class IosSampleController(factory: IosAppFactory = IosAppFactory()) {
    private val summaryViewModel = factory.createSummaryViewModel()
    private val entryViewModel = factory.createEntryViewModel()

    fun loadTodaySummary() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        summaryViewModel.dispatch(SummaryIntent.LoadDaily(today))
    }

    fun currentSummaryText(): String = summaryViewModel.state.value.text

    fun createSampleEntry(text: String) {
        entryViewModel.dispatch(EntryIntent.EditText(text))
        entryViewModel.dispatch(EntryIntent.Save)
    }
}
