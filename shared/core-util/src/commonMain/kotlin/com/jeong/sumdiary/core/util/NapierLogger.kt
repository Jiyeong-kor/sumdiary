package com.jeong.sumdiary.core.util

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

object NapierLogger {
    fun init(enableDebug: Boolean) {
        if (enableDebug) {
            Napier.base(DebugAntilog())
        } else {
            Napier.takeLogarithm()
        }
    }
}
