package com.jeong.sumdiary.data.summary

import app.cash.sqldelight.db.SqlDriver
import com.jeong.sumdiary.data.summary.db.Summary
import com.jeong.sumdiary.data.summary.db.SummaryDatabase
import com.jeong.sumdiary.data.summary.db.SummaryQueries
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.summary.Summary as DomainSummary
import com.jeong.sumdiary.domain.summary.SummaryRepository
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

class SummaryRepositoryImpl(
    private val queries: SummaryQueries,
    private val engine: SummarizerEngine,
) : SummaryRepository {
    override suspend fun summarize(entries: List<DiaryEntry>): DomainSummary {
        val periodStart = entries.minOfOrNull { it.date } ?: today()
        val periodEnd = entries.maxOfOrNull { it.date } ?: periodStart
        val summaryText = engine.run(entries.map { it.content })
        val summaryType = inferType(periodStart, periodEnd)
        val summary = DomainSummary(
            type = summaryType,
            periodStart = periodStart,
            periodEnd = periodEnd,
            text = summaryText,
            emotions = analyzeEmotions(summaryText),
        )
        queries.upsert(
            type = summary.type.name,
            periodStart = summary.periodStart.toString(),
            periodEnd = summary.periodEnd.toString(),
            summaryText = summary.text,
        )
        return summary
    }

    override suspend fun get(periodStart: LocalDate, periodEnd: LocalDate): DomainSummary? {
        return queries.selectByPeriod(
            periodStart = periodStart.toString(),
            periodEnd = periodEnd.toString(),
        ).executeAsOneOrNull()?.toDomain()
    }

    private fun Summary.toDomain(): DomainSummary = DomainSummary(
        type = SummaryType.valueOf(type),
        periodStart = LocalDate.parse(periodStart),
        periodEnd = LocalDate.parse(periodEnd),
        text = summaryText,
        emotions = analyzeEmotions(summaryText),
    )

    private fun inferType(start: LocalDate, end: LocalDate): SummaryType {
        val days = start.daysUntil(end)
        return when {
            days <= 0 -> SummaryType.DAILY
            days < 7 -> SummaryType.WEEKLY
            else -> SummaryType.MONTHLY
        }
    }

    private fun analyzeEmotions(text: String): List<String> {
        val lowered = text.lowercase()
        return buildList {
            if (listOf("행복", "happy", "기쁨").any { lowered.contains(it) }) add("기쁨")
            if (listOf("슬픔", "sad").any { lowered.contains(it) }) add("슬픔")
            if (isEmpty()) add("중립")
        }
    }

    private fun today(): LocalDate {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return now.date
    }

    companion object {
        fun create(
            driver: SqlDriver,
            engine: SummarizerEngine = PlaceholderSummarizerEngine()
        ): SummaryRepositoryImpl {
            val database = SummaryDatabase(driver)
            return SummaryRepositoryImpl(database.summaryQueries, engine)
        }
    }
}

fun createSummaryRepository(
    driver: SqlDriver,
    engine: SummarizerEngine = PlaceholderSummarizerEngine(),
): SummaryRepository = SummaryRepositoryImpl.create(driver, engine)
