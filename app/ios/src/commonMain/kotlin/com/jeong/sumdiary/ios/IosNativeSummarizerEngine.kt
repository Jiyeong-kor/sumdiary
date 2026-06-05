package com.jeong.sumdiary.ios

import com.jeong.sumdiary.data.summary.SummarizerEngine
import com.jeong.sumdiary.data.summary.SummarizerResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface IosNativeSummarizer {
    val isSupported: Boolean

    fun summarize(text: String, completion: (String?) -> Unit)
}

class IosFoundationModelsSummarizerEngine(
    private val nativeSummarizer: IosNativeSummarizer
) : SummarizerEngine {
    override val isSupported: Boolean
        get() = nativeSummarizer.isSupported

    override suspend fun run(texts: List<String>): SummarizerResult {
        if (!isSupported) {
            return SummarizerResult.Unsupported
        }

        val text = texts
            .joinToString(separator = "\n") { it.trim() }
            .trim()

        if (text.length < MIN_ARTICLE_CHARACTERS) {
            return SummarizerResult.Unsupported
        }

        val summary = suspendCoroutine { continuation ->
            var didResume = false
            nativeSummarizer.summarize(text) { value ->
                if (!didResume) {
                    didResume = true
                    continuation.resume(value?.trim())
                }
            }
        }

        return if (summary.isNullOrBlank()) {
            SummarizerResult.Unsupported
        } else {
            SummarizerResult.Success(summary)
        }
    }

    private companion object {
        const val MIN_ARTICLE_CHARACTERS = 400
    }
}
