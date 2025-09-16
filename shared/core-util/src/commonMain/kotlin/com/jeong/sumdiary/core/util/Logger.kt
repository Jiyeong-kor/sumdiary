package com.jeong.sumdiary.core.util

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

object Logger {
    fun initialize(tag: String = "SumDiary") {
        Napier.base(DebugAntilog(tag))
        Napier.d(message = "Napier logger initialized.")
    }

    fun d(message: String, throwable: Throwable? = null) {
        Napier.d(message, throwable)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Napier.e(message, throwable)
    }
}
