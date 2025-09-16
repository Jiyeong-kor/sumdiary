package com.jeong.sumdiary.data.summary

interface SummarizerEngine {
    suspend fun run(texts: List<String>): String
}

class PlaceholderSummarizerEngine : SummarizerEngine {
    override suspend fun run(texts: List<String>): String {
        if (texts.isEmpty()) return "오늘의 기록이 없어요."
        val sentences = texts.map { text ->
            val firstPeriod = text.indexOf('.')
            if (firstPeriod >= 0) {
                text.substring(0, firstPeriod + 1)
            } else {
                text
            }
        }
        val joined = sentences.joinToString(separator = " ").trim()
        return if (joined.length <= 200) joined else joined.take(200) + "…"
    }
}
