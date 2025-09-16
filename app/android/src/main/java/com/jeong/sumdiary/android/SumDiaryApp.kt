package com.jeong.sumdiary.android

import android.app.Application
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jeong.sumdiary.core.util.Logger
import com.jeong.sumdiary.data.diary.createDiaryRepository
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.summary.PlaceholderSummarizerEngine
import com.jeong.sumdiary.data.summary.createSummaryRepository
import com.jeong.sumdiary.data.summary.db.SummaryDatabase
import com.jeong.sumdiary.domain.summary.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.GenerateWeeklySummary
import com.jeong.sumdiary.feature.entry.EntryViewModel
import com.jeong.sumdiary.feature.summary.SummaryViewModel

class SumDiaryApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        Logger.initialize()
        container = AppContainer(this)
    }
}

class AppContainer(app: Application) {
    private val diaryDriver = AndroidSqliteDriver(DiaryDatabase.Schema, app, "diary.db")
    private val summaryDriver = AndroidSqliteDriver(SummaryDatabase.Schema, app, "summary.db")

    private val diaryRepository = createDiaryRepository(diaryDriver)
    private val summaryRepository =
        createSummaryRepository(summaryDriver, PlaceholderSummarizerEngine())

    private val generateDailySummary = GenerateDailySummary(diaryRepository, summaryRepository)
    private val generateWeeklySummary = GenerateWeeklySummary(diaryRepository, summaryRepository)

    val entryViewModel: EntryViewModel = EntryViewModel(diaryRepository)
    val summaryViewModel: SummaryViewModel =
        SummaryViewModel(generateDailySummary, generateWeeklySummary)
}
