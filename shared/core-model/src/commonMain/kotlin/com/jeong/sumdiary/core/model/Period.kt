package com.jeong.sumdiary.core.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Period(
    val start: LocalDate,
    val end: LocalDate
)

@Serializable
@JvmInline
value class DiaryEntryId(val value: String)
