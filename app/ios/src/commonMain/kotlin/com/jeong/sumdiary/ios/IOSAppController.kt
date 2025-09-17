package com.jeong.sumdiary.ios

import app.cash.sqldelight.driver.native.NativeSqliteDriver
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
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.dayOfWeek
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class IOSAppController {
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider
    private val diaryRepository: DiaryRepository
    private val summaryRepository: SummaryRepository
    private val generateDailySummary: GenerateDailySummary
    private val generateWeeklySummary: GenerateWeeklySummary

    init {
        val diaryDriver = NativeSqliteDriver(DiaryDatabase.Schema, "diary.db")
        val summaryDriver = NativeSqliteDriver(SummaryDatabase.Schema, "summary.db")
        val diaryDatabase = DiaryDatabase(diaryDriver)
        val summaryDatabase = SummaryDatabase(summaryDriver)
        val summarizer = PlaceholderSummarizerEngine()

        diaryRepository = DiaryRepositoryImpl(diaryDatabase, dispatcherProvider.io)
        summaryRepository =
            SummaryRepositoryImpl(summaryDatabase, summarizer, dispatcherProvider.io)
        generateDailySummary = GenerateDailySummary(diaryRepository, summaryRepository)
        generateWeeklySummary = GenerateWeeklySummary(diaryRepository, summaryRepository)

        seedSampleEntries()
    }

    fun greeting(): String = "SumDiary iOS 준비 중입니다."

    fun loadDailySummary(): String = runBlocking {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        generateDailySummary(today).text
    }

    fun loadWeeklySummary(): String = runBlocking {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val weekStart = today.minus(DatePeriod(days = today.dayOfWeek.ordinal))
        generateWeeklySummary(weekStart).text
    }

    private fun seedSampleEntries() = runBlocking {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        if (diaryRepository.getByDate(today.date).isEmpty()) {
            diaryRepository.upsert(
                DiaryEntry(
                    id = "ios-sample",
                    date = today.date,
                    time = today.time,
                    content = "iOS에서 작성된 첫 번째 일기입니다.",
                )
            )
        }
    }
}
