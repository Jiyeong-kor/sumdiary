package com.jeong.sumdiary.core.util

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

object Logger {
    fun init(enableDebug: Boolean) {
        val antilog: Antilog = if (enableDebug) DebugAntilog() else object : Antilog() {
            override fun performLog(
                priority: Napier.Level,
                tag: String?,
                throwable: Throwable?,
                message: String?
            ) = Unit
        }
        Napier.base(antilog)
    }

    fun d(message: String) {
        Napier.d(message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Napier.e(message, throwable)
    }
}
