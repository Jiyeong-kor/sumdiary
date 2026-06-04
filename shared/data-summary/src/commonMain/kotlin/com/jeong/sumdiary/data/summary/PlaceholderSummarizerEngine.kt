package com.jeong.sumdiary.data.summary

import kotlinx.coroutines.delay
import kotlin.math.min

class UnsupportedSummarizerEngine : SummarizerEngine {
    override val isSupported: Boolean = false

    override suspend fun run(texts: List<String>): SummarizerResult =
        SummarizerResult.Unsupported
}

class FakeSummarizerEngine : SummarizerEngine {
    override val isSupported: Boolean = true

    override suspend fun run(texts: List<String>): SummarizerResult {
        delay(50)
        val sentences = texts.mapNotNull { text ->
            text.split('.')
                .map { it.trim() }
                .firstOrNull { it.isNotEmpty() }
        }
        val combined = sentences.joinToString(separator = ". ")
        return SummarizerResult.Success(combined.take(200))
    }
}

class LocalSummarizerEngine : SummarizerEngine {
    override val isSupported: Boolean = true

    override suspend fun run(texts: List<String>): SummarizerResult {
        val normalized = texts
            .flatMap { it.toSentences() }
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (normalized.isEmpty()) {
            return SummarizerResult.Unsupported
        }

        val selected = normalized
            .sortedWith(
                compareByDescending<String> { it.score() }
                    .thenBy { normalized.indexOf(it) }
            )
            .take(min(3, normalized.size))
            .sortedBy { normalized.indexOf(it) }

        val summary = selected.joinToString(separator = " ").fitToLength(maxLength = 240)
        return SummarizerResult.Success(summary)
    }

    private fun String.toSentences(): List<String> =
        split('.', '!', '?', '\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    private fun String.score(): Int {
        val emotionScore = listOf(
            "행복",
            "기쁨",
            "감사",
            "슬픔",
            "우울",
            "피곤",
            "걱정",
            "화나",
            "좋",
            "싫"
        ).count { contains(it, ignoreCase = true) } * 3
        val lengthScore = length.coerceAtMost(80)
        return emotionScore + lengthScore
    }

    private fun String.fitToLength(maxLength: Int): String =
        if (length <= maxLength) {
            this
        } else {
            take(maxLength - 1).trimEnd() + "…"
        }
}
