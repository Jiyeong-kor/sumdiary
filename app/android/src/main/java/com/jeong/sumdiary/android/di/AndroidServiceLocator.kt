package com.jeong.sumdiary.android.di

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jeong.sumdiary.core.util.DefaultDispatcherProvider
import com.jeong.sumdiary.core.util.DispatcherProvider
import com.jeong.sumdiary.data.diary.DiaryDatabase
import com.jeong.sumdiary.data.diary.DiaryRepositoryImpl
import com.jeong.sumdiary.data.summary.PlaceholderSummarizerEngine
import com.jeong.sumdiary.data.summary.SummaryDatabase
import com.jeong.sumdiary.data.summary.SummaryRepositoryImpl
import com.jeong.sumdiary.domain.diary.model.DiaryEntry
import com.jeong.sumdiary.domain.diary.repository.DiaryRepository
import com.jeong.sumdiary.domain.summary.repository.SummaryRepository
import com.jeong.sumdiary.domain.summary.usecase.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.usecase.GenerateWeeklySummary
import com.jeong.sumdiary.feature.entry.EntryViewModel
import com.jeong.sumdiary.feature.summary.SummaryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object AndroidServiceLocator {
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var diaryRepository: DiaryRepository
    private lateinit var summaryRepository: SummaryRepository
    private lateinit var generateDailySummary: GenerateDailySummary
    private lateinit var generateWeeklySummary: GenerateWeeklySummary

    fun initialize(context: Context) {
        dispatcherProvider = DefaultDispatcherProvider
        val diaryDriver = AndroidSqliteDriver(DiaryDatabase.Schema, context, "diary.db")
        val summaryDriver = AndroidSqliteDriver(SummaryDatabase.Schema, context, "summary.db")
        val diaryDatabase = DiaryDatabase(diaryDriver)
        val summaryDatabase = SummaryDatabase(summaryDriver)
        val summarizer = PlaceholderSummarizerEngine()

        diaryRepository = DiaryRepositoryImpl(diaryDatabase, dispatcherProvider.io)
        summaryRepository =
            SummaryRepositoryImpl(summaryDatabase, summarizer, dispatcherProvider.io)
        generateDailySummary = GenerateDailySummary(diaryRepository, summaryRepository)
        generateWeeklySummary = GenerateWeeklySummary(diaryRepository, summaryRepository)

        seedSampleEntriesIfEmpty()
    }

    fun provideEntryViewModel(): EntryViewModel {
        return EntryViewModel(diaryRepository, dispatcherProvider)
    }

    fun provideSummaryViewModel(): SummaryViewModel {
        return SummaryViewModel(generateDailySummary, generateWeeklySummary, dispatcherProvider)
    }

    fun diaryRepository(): DiaryRepository = diaryRepository

    fun summaryRepository(): SummaryRepository = summaryRepository

    private fun seedSampleEntriesIfEmpty() {
        val scope = CoroutineScope(dispatcherProvider.io + SupervisorJob())
        scope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            if (diaryRepository.getByDate(today).isEmpty()) {
                repeat(3) { index ->
                    val content = "샘플 일기 ${'$'}{index + 1}: 오늘의 감정을 기록해보세요."
                    diaryRepository.upsert(
                        DiaryEntry(
                            id = "sample-${'$'}index",
                            date = today,
                            time = Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault()).time,
                            content = content,
                        ),
                    )
                }
            }
        }
    }
}
