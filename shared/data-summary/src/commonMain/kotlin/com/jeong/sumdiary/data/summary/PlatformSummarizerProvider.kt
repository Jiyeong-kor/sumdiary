package com.jeong.sumdiary.data.summary

object PlatformSummarizerProvider {
    fun create(): SummarizerEngine = UnsupportedSummarizerEngine()

    fun createFakeForDevelopment(): SummarizerEngine = FakeSummarizerEngine()
}
