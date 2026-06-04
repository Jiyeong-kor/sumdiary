package com.jeong.sumdiary.data.summary

import kotlinx.coroutines.delay

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
