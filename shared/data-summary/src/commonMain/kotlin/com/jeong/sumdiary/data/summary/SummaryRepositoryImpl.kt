package com.jeong.sumdiary.data.summary

import com.jeong.sumdiary.domain.diary.model.DiaryEntry
import com.jeong.sumdiary.domain.summary.model.Summary
import com.jeong.sumdiary.domain.summary.model.SummaryType
import com.jeong.sumdiary.domain.summary.repository.SummaryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.fromEpochDays

class SummaryRepositoryImpl(
    private val database: SummaryDatabase,
    private val summarizerEngine: SummarizerEngine,
    private val dispatcher: CoroutineDispatcher
) : SummaryRepository {

    private val queries get() = database.summaryQueries

    override suspend fun summarize(entries: List<DiaryEntry>): Summary {
        val summaryText = summarizerEngine.run(entries.map { it.content })
        val dates = entries.map { it.date }.sorted()
        val periodStart = dates.firstOrNull() ?: LocalDate.fromEpochDays(0)
        val periodEnd = dates.lastOrNull() ?: periodStart
        val type = when {
            dates.isEmpty() -> SummaryType.DAILY
            dates.toSet().size == 1 -> SummaryType.DAILY
            dates.toSet().size <= 7 -> SummaryType.WEEKLY
            else -> SummaryType.MONTHLY
        }
        val emotions = deriveEmotions(summaryText)
        val summary = Summary(
            type = type,
            periodStart = periodStart,
            periodEnd = periodEnd,
            text = summaryText,
            emotions = emotions
        )
        withContext(dispatcher) {
            queries.insertSummary(
                type = summary.type.name,
                periodStart = summary.periodStart.toString(),
                periodEnd = summary.periodEnd.toString(),
                summaryText = summary.text,
            )
        }
        return summary
    }

    override suspend fun get(periodStart: LocalDate, periodEnd: LocalDate): Summary? {
        return withContext(dispatcher) {
            queries.getSummary(
                periodStart = periodStart.toString(),
                periodEnd = periodEnd.toString(),
            ).executeAsOneOrNull()?.let { entity ->
                Summary(
                    type = runCatching { SummaryType.valueOf(entity.type) }.getOrDefault(SummaryType.DAILY),
                    periodStart = LocalDate.parse(entity.periodStart),
                    periodEnd = LocalDate.parse(entity.periodEnd),
                    text = entity.summaryText,
                    emotions = deriveEmotions(entity.summaryText),
                )
            }
        }
    }

    private fun deriveEmotions(text: String): List<String> {
        if (text.isBlank()) return emptyList()
        val lower = text.lowercase()
        val candidates = buildList {
            if (listOf("행복", "즐겁", "기쁨", "happy", "joy").any { lower.contains(it) }) add("기쁨")
            if (listOf("슬프", "sad", "우울").any { lower.contains(it) }) add("슬픔")
            if (listOf("화", "angry", "짜증").any { lower.contains(it) }) add("분노")
            if (listOf("불안", "anx", "걱정").any { lower.contains(it) }) add("불안")
        }
        return if (candidates.isEmpty()) listOf("중립") else candidates.distinct()
    }
}
