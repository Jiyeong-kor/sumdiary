package com.jeong.sumdiary.data.summary

interface SummarizerEngine {
    suspend fun run(texts: List<String>): String
}
