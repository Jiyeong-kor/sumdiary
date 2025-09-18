package com.jeong.sumdiary.core.util.logging

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

object Logger {
    fun init(antilog: Antilog = DebugAntilog()) {
        Napier.base(antilog)
    }

    fun d(message: String, tag: String? = null) {
        Napier.d(message = message, tag = tag)
    }

    fun e(message: String, throwable: Throwable? = null, tag: String? = null) {
        Napier.e(message = message, throwable = throwable, tag = tag)
    }
}
