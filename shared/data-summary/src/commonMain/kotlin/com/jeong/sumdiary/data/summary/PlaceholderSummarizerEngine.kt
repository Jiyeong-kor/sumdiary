package com.jeong.sumdiary.data.summary

import kotlinx.coroutines.delay

class PlaceholderSummarizerEngine : SummarizerEngine {
    override suspend fun run(texts: List<String>): String {
        delay(50)
        if (texts.isEmpty()) return "오늘은 기록이 없습니다."
        val sentences = texts.mapNotNull { text ->
            text.split('.')
                .map { it.trim() }
                .firstOrNull { it.isNotEmpty() }
        }
        val combined = sentences.joinToString(separator = ". ")
        return combined.take(200)
    }
}
