package com.jeong.sumdiary.data.summary

import com.jeong.sumdiary.data.summary.db.SelectSummary
import com.jeong.sumdiary.data.summary.db.SummaryDatabase
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.summary.Summary
import com.jeong.sumdiary.domain.summary.SummaryRepository
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

class SummaryRepositoryImpl(
    private val database: SummaryDatabase,
    private val dispatcher: CoroutineDispatcher,
    private val summarizerEngine: SummarizerEngine
) : SummaryRepository {

    private val queries = database.summaryQueries

    override suspend fun summarize(entries: List<DiaryEntry>): Summary {
        val periodStart = entries.minOfOrNull { it.date } ?: LocalDate(1970, 1, 1)
        val periodEnd = entries.maxOfOrNull { it.date } ?: periodStart
        val summaryText = summarizerEngine.run(entries.map { it.content })
        val type = deriveType(periodStart, periodEnd)
        val emotions = deriveEmotions(summaryText)
        withContext(dispatcher) {
            queries.insertSummary(
                type = type.name,
                periodStart = periodStart.toString(),
                periodEnd = periodEnd.toString(),
                summaryText = summaryText
            )
        }
        return Summary(
            type = type,
            periodStart = periodStart,
            periodEnd = periodEnd,
            text = summaryText,
            emotions = emotions
        )
    }

    override suspend fun get(periodStart: LocalDate, periodEnd: LocalDate): Summary? =
        withContext(dispatcher) {
            queries.selectSummary(
                periodStart = periodStart.toString(),
                periodEnd = periodEnd.toString()
            ).executeAsOneOrNull()?.toSummary()
        }

    private fun deriveType(start: LocalDate, end: LocalDate): SummaryType {
        val days = start.daysUntil(end)
        return when {
            days <= 0 -> SummaryType.DAILY
            days < 7 -> SummaryType.WEEKLY
            else -> SummaryType.MONTHLY
        }
    }

    private fun deriveEmotions(text: String): List<String> {
        val lower = text.lowercase()
        val emotions = mutableListOf<String>()
        if (listOf("행복", "기쁨", "즐거움").any { it in text }) {
            emotions += "positive"
        }
        if (listOf("슬픔", "우울", "피곤").any { it in text }) {
            emotions += "sad"
        }
        if (lower.contains("화나")) {
            emotions += "angry"
        }
        if (emotions.isEmpty()) {
            emotions += "neutral"
        }
        return emotions
    }

    private fun SelectSummary.toSummary(): Summary = Summary(
        type = SummaryType.valueOf(type),
        periodStart = LocalDate.parse(periodStart),
        periodEnd = LocalDate.parse(periodEnd),
        text = summaryText,
        emotions = deriveEmotions(summaryText)
    )
}
