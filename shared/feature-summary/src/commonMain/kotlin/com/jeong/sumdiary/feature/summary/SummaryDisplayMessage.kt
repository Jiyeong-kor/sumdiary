package com.jeong.sumdiary.feature.summary

data class SummaryDisplayMessage(
    val label: String,
    val title: String,
    val description: String
) {
    fun bodyText(contentText: String): String =
        if (contentText.isNotBlank()) {
            contentText
        } else {
            title
        }
}

val SummaryUiStatus.displayMessage: SummaryDisplayMessage
    get() = when (this) {
        SummaryUiStatus.NOT_GENERATED -> SummaryDisplayMessage(
            label = "대기",
            title = "아직 생성한 요약이 없어요",
            description = "오늘 또는 이번 주 기록을 선택해 요약을 만들 수 있어요."
        )
        SummaryUiStatus.LOADING -> SummaryDisplayMessage(
            label = "생성 중",
            title = "요약을 만들고 있어요",
            description = "일기 원문은 기본적으로 이 기기 안에서 처리해요."
        )
        SummaryUiStatus.CONTENT -> SummaryDisplayMessage(
            label = "완료",
            title = "",
            description = ""
        )
        SummaryUiStatus.NO_ENTRIES -> SummaryDisplayMessage(
            label = "기록 없음",
            title = "이 기간에는 기록이 없어요",
            description = "기록이 생기면 요약을 만들 수 있어요."
        )
        SummaryUiStatus.UNSUPPORTED -> SummaryDisplayMessage(
            label = "미지원",
            title = "이 기기에서는 아직 요약을 만들 수 없어요",
            description = "지원 기기와 OS 조건에 따라 온디바이스 AI 사용 가능 여부가 달라질 수 있어요."
        )
        SummaryUiStatus.FAILED -> SummaryDisplayMessage(
            label = "실패",
            title = "요약을 만들지 못했어요",
            description = "잠시 후 다시 시도해 주세요."
        )
    }
