package com.jeong.sumdiary.ios

import com.jeong.sumdiary.core.util.time.TimeProvider
import kotlinx.datetime.TimeZone

class IosController {
    fun initialText(): String {
        val today = TimeProvider.today(TimeZone.currentSystemDefault())
        return "SumDiary iOS - 오늘은 ${'$'}today"
    }
}
