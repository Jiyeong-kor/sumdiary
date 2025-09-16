package com.jeong.sumdiary.domain.diary

fun List<DiaryEntry>.sortedByTimestamp(): List<DiaryEntry> = sortedWith(
    compareBy<DiaryEntry> { it.date }.thenBy { it.time }
)
