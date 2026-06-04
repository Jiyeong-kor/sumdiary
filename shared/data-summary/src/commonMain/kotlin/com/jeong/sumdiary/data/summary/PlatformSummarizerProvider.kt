package com.jeong.sumdiary.data.summary

object PlatformSummarizerProvider {
    fun create(): SummarizerEngine = LocalSummarizerEngine()

    fun createFakeForDevelopment(): SummarizerEngine = FakeSummarizerEngine()
}
