package com.jeong.sumdiary.ios

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.jeong.sumdiary.core.util.DefaultDispatchersProvider
import com.jeong.sumdiary.data.diary.DiaryRepositoryImpl
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.summary.SummarizerEngine
import com.jeong.sumdiary.data.summary.SummaryRepositoryImpl
import com.jeong.sumdiary.data.summary.db.SummaryDatabase
import com.jeong.sumdiary.domain.summary.usecase.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.usecase.GenerateWeeklySummary
import com.jeong.sumdiary.feature.entry.EntryIntent
import com.jeong.sumdiary.feature.entry.EntryViewModel
import com.jeong.sumdiary.feature.summary.SummaryIntent
import com.jeong.sumdiary.feature.summary.SummaryState
import com.jeong.sumdiary.feature.summary.SummaryUiStatus
import com.jeong.sumdiary.feature.summary.SummaryViewModel
import com.jeong.sumdiary.feature.summary.displayMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class IosAppFactory(
    private val summarizerEngine: SummarizerEngine
) {
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
        summarizerEngine
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

class IosSampleController(factory: IosAppFactory) {
    private val summaryViewModel = factory.createSummaryViewModel()
    private val entryViewModel = factory.createEntryViewModel()
    private val callbackScope = CoroutineScope(SupervisorJob() + DefaultDispatchersProvider.io)

    @OptIn(ExperimentalTime::class)
    fun loadTodaySummary() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        summaryViewModel.dispatch(SummaryIntent.LoadDaily(today))
    }

    @OptIn(ExperimentalTime::class)
    fun loadTodaySummary(completion: (String) -> Unit) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        summaryViewModel.dispatch(SummaryIntent.LoadDaily(today))
        callbackScope.launch {
            val state = summaryViewModel.state
                .filter { summaryState ->
                    summaryState.period.first == today &&
                        summaryState.period.second == today &&
                        summaryState.status != SummaryUiStatus.LOADING
                }
                .first()
            completion(state.toDisplayText())
        }
    }

    fun currentSummaryText(): String = summaryViewModel.state.value.text

    fun createSampleEntry(text: String) {
        entryViewModel.dispatch(EntryIntent.EditText(text))
        entryViewModel.dispatch(EntryIntent.Save)
    }

    private fun SummaryState.toDisplayText(): String =
        when (status) {
            SummaryUiStatus.CONTENT -> text
            else -> status.displayMessage.bodyText(text)
        }
}
