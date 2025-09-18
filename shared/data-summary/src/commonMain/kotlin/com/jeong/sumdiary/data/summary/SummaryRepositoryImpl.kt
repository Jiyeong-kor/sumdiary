package com.jeong.sumdiary.data.summary

import com.jeong.sumdiary.data.summary.db.Summary
import com.jeong.sumdiary.data.summary.db.SummaryQueries
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.summary.Summary as SummaryModel
import com.jeong.sumdiary.domain.summary.SummaryRepository
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.datetime.LocalDate

class SummaryRepositoryImpl(
    private val queries: SummaryQueries,
    private val summarizerEngine: SummarizerEngine
) : SummaryRepository {

    override suspend fun summarize(entries: List<DiaryEntry>): SummaryModel {
        val periodStart = entries.minOfOrNull { it.date } ?: today()
        val periodEnd = entries.maxOfOrNull { it.date } ?: periodStart
        val summaryType = resolveSummaryType(periodStart, periodEnd)
        val summaryText = summarizerEngine.run(entries.map { it.content })
        val emotions = inferEmotions(entries)

        val summary = SummaryModel(
            type = summaryType,
            periodStart = periodStart,
            periodEnd = periodEnd,
            text = summaryText,
            emotions = emotions
        )

        queries.insertSummary(
            type = summary.type.name,
            periodStart = summary.periodStart.toString(),
            periodEnd = summary.periodEnd.toString(),
            summaryText = summary.text
        )
        return summary
    }

    override suspend fun get(periodStart: LocalDate, periodEnd: LocalDate): SummaryModel? {
        return queries.selectSummaryByPeriod(
            periodStart = periodStart.toString(),
            periodEnd = periodEnd.toString(),
        ).executeAsOneOrNull()?.toDomain()
    }

    private fun Summary.toDomain(): SummaryModel {
        return SummaryModel(
            type = runCatching { SummaryType.valueOf(type) }
                .getOrDefault(SummaryType.DAILY),
            periodStart = LocalDate.parse(periodStart),
            periodEnd = LocalDate.parse(periodEnd),
            text = summaryText,
            emotions = emptyList()
        )
    }

    private fun resolveSummaryType(start: LocalDate, end: LocalDate): SummaryType {
        val days = start.daysUntil(end) + 1
        return when {
            days <= 1 -> SummaryType.DAILY
            days <= 7 -> SummaryType.WEEKLY
            else -> SummaryType.MONTHLY
        }
    }

    private fun inferEmotions(entries: List<DiaryEntry>): List<String> {
        if (entries.isEmpty()) return listOf("중립")
        val texts = entries.joinToString(separator = " ") { it.content.lowercase() }
        val detected = EMOTION_KEYWORDS.mapNotNull { (keyword, label) ->
            if (texts.contains(keyword)) label else null
        }.distinct()
        return if (detected.isEmpty()) listOf("중립") else detected
    }

    private fun today(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.UTC).date
    }

    companion object {
        private val EMOTION_KEYWORDS = mapOf(
            "happy" to "기쁨",
            "즐거움" to "기쁨",
            "기쁜" to "기쁨",
            "sad" to "슬픔",
            "슬픔" to "슬픔",
            "우울" to "슬픔",
            "angry" to "분노",
            "화가남" to "분노",
            "화남" to "분노",
            "tired" to "피로",
            "피곤" to "피로"
        )
    }
}

object SummaryRepositoryFactory {
    fun create(
        database: SummaryDatabase,
        engine: SummarizerEngine,
    ): SummaryRepository {
        return SummaryRepositoryImpl(database.summaryQueries, engine)
    }
}
