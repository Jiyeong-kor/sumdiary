package com.jeong.sumdiary.android

import android.content.Context
import app.cash.sqldelight.android.AndroidSqliteDriver
import com.jeong.sumdiary.data.diary.DiaryDatabaseFactory
import com.jeong.sumdiary.data.diary.DiaryRepositoryFactory
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.summary.PlaceholderSummarizerEngine
import com.jeong.sumdiary.data.summary.SummaryDatabaseFactory
import com.jeong.sumdiary.data.summary.SummaryRepositoryFactory
import com.jeong.sumdiary.data.summary.db.SummaryDatabase
import com.jeong.sumdiary.domain.diary.DiaryRepository
import com.jeong.sumdiary.domain.summary.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.GenerateWeeklySummary
import com.jeong.sumdiary.domain.summary.SummaryRepository

class AppContainer(context: Context) {
    private val diaryDriver = AndroidSqliteDriver(DiaryDatabase.Schema, context, "diary.db")
    private val summaryDriver = AndroidSqliteDriver(SummaryDatabase.Schema, context, "summary.db")

    private val diaryDatabase = DiaryDatabaseFactory(diaryDriver).create()
    private val summaryDatabase = SummaryDatabaseFactory(summaryDriver).create()

    val diaryRepository: DiaryRepository = DiaryRepositoryFactory.create(diaryDatabase)
    private val summarizerEngine = PlaceholderSummarizerEngine()
    val summaryRepository: SummaryRepository =
        SummaryRepositoryFactory.create(summaryDatabase, summarizerEngine)

    val generateDailySummary: GenerateDailySummary =
        GenerateDailySummary(diaryRepository, summaryRepository)
    val generateWeeklySummary: GenerateWeeklySummary =
        GenerateWeeklySummary(diaryRepository, summaryRepository)
}
