package com.jeong.sumdiary.domain.diary.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * 일기 엔트리를 나타내는 데이터 클래스
 *
 * @property id 고유 식별자
 * @property date 작성 날짜
 * @property time 작성 시간
 * @property content 일기 내용
 */
data class DiaryEntry(
    val id: String,
    val date: LocalDate,
    val time: LocalTime,
    val content: String
)
