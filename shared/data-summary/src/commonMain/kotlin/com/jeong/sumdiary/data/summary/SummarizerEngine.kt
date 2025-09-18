package com.jeong.sumdiary.data.summary

fun interface SummarizerEngine {
    suspend fun run(texts: List<String>): String
}

class PlaceholderSummarizerEngine : SummarizerEngine {
    override suspend fun run(texts: List<String>): String {
        if (texts.isEmpty()) return ""
        val firstSentences = texts.map { content ->
            val trimmed = content.trim()
            if (trimmed.isEmpty()) {
                ""
            } else {
                SENTENCE_DELIMITERS.find(trimmed)?.value?.let { delimiter ->
                    trimmed.substringBefore(delimiter) + delimiter
                } ?: trimmed
            }
        }
        val combined = firstSentences.joinToString(separator = " ") { it.trim() }
        return if (combined.length <= MAX_LENGTH) combined else combined.take(MAX_LENGTH)
    }

    private companion object {
        private const val MAX_LENGTH = 200
        private val SENTENCE_DELIMITERS = Regex("[.!?]")
    }
}
