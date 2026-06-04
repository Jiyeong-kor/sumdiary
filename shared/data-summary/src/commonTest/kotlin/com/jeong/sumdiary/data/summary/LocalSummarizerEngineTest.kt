package com.jeong.sumdiary.data.summary

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LocalSummarizerEngineTest {
    private val engine = LocalSummarizerEngine()

    @Test
    fun returnsUnsupportedForBlankInput() = runTest {
        val result = engine.run(listOf(" ", "\n"))

        assertEquals(SummarizerResult.Unsupported, result)
    }

    @Test
    fun summarizesMeaningfulSentencesWithoutNetworkFallback() = runTest {
        val result = engine.run(
            listOf(
                "오늘은 아침 산책이 좋았다. 오후에는 일이 많아서 피곤했다.",
                "저녁에는 가족과 이야기하며 감사한 마음이 들었다."
            )
        )

        val success = assertIs<SummarizerResult.Success>(result)
        assertTrue(success.text.contains("아침 산책"))
        assertTrue(success.text.length <= 240)
    }
}
