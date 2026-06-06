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
            title = "요약할 기록을 선택해 주세요",
            description = "오늘 또는 이번 주 기록을 선택하면 기기 안에서 요약을 만들어요."
        )
        SummaryUiStatus.LOADING -> SummaryDisplayMessage(
            label = "처리 중",
            title = "기기에서 요약을 만들고 있어요",
            description = "처리가 중단되면 다시 시도할 수 있어요. 일기 원문은 외부 서버로 보내지 않아요."
        )
        SummaryUiStatus.CONTENT -> SummaryDisplayMessage(
            label = "완료",
            title = "",
            description = ""
        )
        SummaryUiStatus.NO_ENTRIES -> SummaryDisplayMessage(
            label = "기록 부족",
            title = "요약할 기록이 아직 없어요",
            description = "짧게 남기기를 먼저 하고 다시 요약해 주세요."
        )
        SummaryUiStatus.UNSUPPORTED -> SummaryDisplayMessage(
            label = "지원 안 됨",
            title = "이 기기에서는 아직 요약을 만들 수 없어요",
            description = "지원 기기와 OS 조건에 따라 온디바이스 AI 사용 가능 여부가 달라질 수 있어요."
        )
        SummaryUiStatus.FAILED -> SummaryDisplayMessage(
            label = "일시 오류",
            title = "요약이 일시적으로 중단됐어요",
            description = "일기 원문은 그대로 보존돼요. 잠시 후 다시 시도해 주세요."
        )
    }
