package com.jeong.sumdiary.android.di

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jeong.sumdiary.core.util.DefaultDispatchersProvider
import com.jeong.sumdiary.data.diary.DiaryRepositoryImpl
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.summary.PlaceholderSummarizerEngine
import com.jeong.sumdiary.data.summary.SummaryRepositoryImpl
import com.jeong.sumdiary.data.summary.db.SummaryDatabase
import com.jeong.sumdiary.domain.summary.usecase.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.usecase.GenerateWeeklySummary
import com.jeong.sumdiary.feature.entry.EntryViewModel
import com.jeong.sumdiary.feature.summary.SummaryViewModel

class AppContainer(context: Context) {

    private val dispatchers = DefaultDispatchersProvider

    private val diaryDatabase = DiaryDatabase(
        AndroidSqliteDriver(DiaryDatabase.Schema, context, "diary.db")
    )

    private val summaryDatabase = SummaryDatabase(
        AndroidSqliteDriver(SummaryDatabase.Schema, context, "summary.db")
    )

    private val diaryRepository = DiaryRepositoryImpl(diaryDatabase, dispatchers.io)
    private val summarizerEngine = PlaceholderSummarizerEngine()
    private val summaryRepository = SummaryRepositoryImpl(
        summaryDatabase,
        dispatchers.io,
        summarizerEngine
    )

    private val generateDailySummary = GenerateDailySummary(diaryRepository, summaryRepository)
    private val generateWeeklySummary = GenerateWeeklySummary(diaryRepository, summaryRepository)

    fun entryViewModel(): EntryViewModel = EntryViewModel(diaryRepository, dispatchers.io)

    fun summaryViewModel(): SummaryViewModel = SummaryViewModel(
        generateDailySummary,
        generateWeeklySummary,
        dispatchers.io
    )
}
