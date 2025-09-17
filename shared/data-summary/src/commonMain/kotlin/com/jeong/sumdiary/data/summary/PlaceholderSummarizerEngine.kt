package com.jeong.sumdiary.data.summary

class PlaceholderSummarizerEngine : SummarizerEngine {
    override suspend fun run(texts: List<String>): String {
        if (texts.isEmpty()) {
            return "요약할 일기가 없습니다."
        }
        val summary = texts.mapNotNull { text ->
            text.split(Regex("(?<=[.!?])\\s+"))
                .firstOrNull()
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
        }.joinToString(separator = " ")
        val normalized = summary.ifBlank { texts.joinToString(separator = " ") { it.trim() } }
        return if (normalized.length <= 200) {
            normalized
        } else {
            normalized.take(200) + "…"
        }
    }
}
