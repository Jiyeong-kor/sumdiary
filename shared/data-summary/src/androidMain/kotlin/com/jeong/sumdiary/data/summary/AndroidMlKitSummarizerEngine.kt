package com.jeong.sumdiary.data.summary

import android.content.Context
import android.os.Build
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.summarization.Summarization
import com.google.mlkit.genai.summarization.SummarizationRequest
import com.google.mlkit.genai.summarization.SummarizerOptions
import kotlinx.coroutines.guava.await

class AndroidMlKitSummarizerEngine(
    context: Context
) : SummarizerEngine {
    override val isSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    private val summarizer by lazy {
        val options = SummarizerOptions.builder(context.applicationContext)
            .setInputType(SummarizerOptions.InputType.ARTICLE)
            .setOutputType(SummarizerOptions.OutputType.ONE_BULLET)
            .setLanguage(SummarizerOptions.Language.KOREAN)
            .setLongInputAutoTruncationEnabled(true)
            .build()
        Summarization.getClient(options)
    }

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

        return try {
            when (summarizer.checkFeatureStatus().await()) {
                FeatureStatus.AVAILABLE,
                FeatureStatus.DOWNLOADABLE,
                FeatureStatus.DOWNLOADING -> summarize(text)
                else -> SummarizerResult.Unsupported
            }
        } catch (_: RuntimeException) {
            SummarizerResult.Unsupported
        }
    }

    private suspend fun summarize(text: String): SummarizerResult {
        val request = SummarizationRequest.builder(text).build()
        val result = summarizer.runInference(request).await()
        val summary = result.summary.trim()
        return if (summary.isBlank()) {
            SummarizerResult.Unsupported
        } else {
            SummarizerResult.Success(summary)
        }
    }

    private companion object {
        const val MIN_ARTICLE_CHARACTERS = 400
    }
}
