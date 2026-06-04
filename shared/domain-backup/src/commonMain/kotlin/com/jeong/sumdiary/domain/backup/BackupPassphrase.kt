package com.jeong.sumdiary.domain.backup

import kotlin.jvm.JvmInline

@JvmInline
value class BackupPassphrase(val value: String) {
    init {
        require(value.length >= MinimumLength) {
            "Backup passphrase must be at least $MinimumLength characters."
        }
    }

    companion object {
        const val MinimumLength = 12
    }
}
