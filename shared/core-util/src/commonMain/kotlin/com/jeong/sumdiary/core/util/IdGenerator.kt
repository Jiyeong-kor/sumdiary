package com.jeong.sumdiary.core.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

object IdGenerator {
    fun create(prefix: String = "entry"): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val randomPart = Random.nextInt(1000, 9999)
        return "$prefix-${'$'}{now.date}-${'$'}{now.time}-${'$'}randomPart"
    }
}
