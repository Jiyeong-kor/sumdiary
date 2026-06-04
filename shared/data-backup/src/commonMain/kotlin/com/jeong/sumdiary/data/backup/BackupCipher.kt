package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupPassphrase

interface BackupCipher {
    val algorithm: String
    val keyDerivation: String
    val iterations: Int

    fun generateSalt(): ByteArray
    fun generateNonce(): ByteArray

    fun encrypt(
        plainBytes: ByteArray,
        passphrase: BackupPassphrase,
        salt: ByteArray,
        nonce: ByteArray
    ): ByteArray?

    fun decrypt(
        encryptedBytes: ByteArray,
        passphrase: BackupPassphrase,
        salt: ByteArray,
        nonce: ByteArray
    ): ByteArray?
}
