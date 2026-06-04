package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupPassphrase

class UnavailableBackupCipher : BackupCipher {
    override val algorithm: String = "AES_256_GCM"
    override val keyDerivation: String = "PBKDF2_HMAC_SHA256"
    override val iterations: Int = 210_000

    override fun generateSalt(): ByteArray = ByteArray(0)
    override fun generateNonce(): ByteArray = ByteArray(0)

    override fun encrypt(
        plainBytes: ByteArray,
        passphrase: BackupPassphrase,
        salt: ByteArray,
        nonce: ByteArray
    ): ByteArray? = null

    override fun decrypt(
        encryptedBytes: ByteArray,
        passphrase: BackupPassphrase,
        salt: ByteArray,
        nonce: ByteArray
    ): ByteArray? = null
}
