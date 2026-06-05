package com.jeong.sumdiary.feature.summary

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SummaryDisplayMessageTest {
    @Test
    fun unsupportedMessageExplainsDeviceAndOsConditions() {
        val message = SummaryUiStatus.UNSUPPORTED.displayMessage

        assertEquals("미지원", message.label)
        assertEquals("이 기기에서는 아직 요약을 만들 수 없어요", message.title)
        assertTrue(message.description.contains("지원 기기"))
        assertTrue(message.description.contains("OS 조건"))
        assertTrue(message.description.contains("온디바이스 AI"))
    }

    @Test
    fun contentBodyTextUsesGeneratedSummary() {
        val message = SummaryUiStatus.CONTENT.displayMessage

        assertEquals("오늘은 차분한 하루였어요.", message.bodyText("오늘은 차분한 하루였어요."))
    }

    @Test
    fun nonContentBodyTextFallsBackToStatusTitle() {
        val message = SummaryUiStatus.FAILED.displayMessage

        assertEquals("요약을 만들지 못했어요", message.bodyText(""))
    }
}
