package com.jeong.sumdiary.data.summary

sealed interface SummarizerResult {
    data class Success(val text: String) : SummarizerResult
    object Unsupported : SummarizerResult
}

interface SummarizerEngine {
    val isSupported: Boolean

    suspend fun run(texts: List<String>): SummarizerResult
}
