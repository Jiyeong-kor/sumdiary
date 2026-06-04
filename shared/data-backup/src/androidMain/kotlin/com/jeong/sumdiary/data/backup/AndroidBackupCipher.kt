package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupPassphrase
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AndroidBackupCipher(
    private val secureRandom: SecureRandom = SecureRandom()
) : BackupCipher {
    override val algorithm: String = "AES_256_GCM"
    override val keyDerivation: String = "PBKDF2_HMAC_SHA256"
    override val iterations: Int = 210_000

    override fun generateSalt(): ByteArray =
        secureRandom.nextBytesArray(SaltSizeBytes)

    override fun generateNonce(): ByteArray =
        secureRandom.nextBytesArray(NonceSizeBytes)

    override fun encrypt(
        plainBytes: ByteArray,
        passphrase: BackupPassphrase,
        salt: ByteArray,
        nonce: ByteArray
    ): ByteArray? = runCatching {
        val cipher = Cipher.getInstance(CipherTransformation)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            deriveKey(passphrase, salt),
            GCMParameterSpec(AuthenticationTagSizeBits, nonce)
        )
        cipher.doFinal(plainBytes)
    }.getOrNull()

    override fun decrypt(
        encryptedBytes: ByteArray,
        passphrase: BackupPassphrase,
        salt: ByteArray,
        nonce: ByteArray
    ): ByteArray? = runCatching {
        val cipher = Cipher.getInstance(CipherTransformation)
        cipher.init(
            Cipher.DECRYPT_MODE,
            deriveKey(passphrase, salt),
            GCMParameterSpec(AuthenticationTagSizeBits, nonce)
        )
        cipher.doFinal(encryptedBytes)
    }.getOrNull()

    private fun deriveKey(
        passphrase: BackupPassphrase,
        salt: ByteArray
    ): SecretKeySpec {
        val spec = PBEKeySpec(
            passphrase.value.toCharArray(),
            salt,
            iterations,
            KeySizeBits
        )
        val key = SecretKeyFactory.getInstance(KeyDerivationAlgorithm)
            .generateSecret(spec)
            .encoded
        return SecretKeySpec(key, KeyAlgorithm)
    }

    private fun SecureRandom.nextBytesArray(size: Int): ByteArray =
        ByteArray(size).also(::nextBytes)

    private companion object {
        const val CipherTransformation = "AES/GCM/NoPadding"
        const val KeyDerivationAlgorithm = "PBKDF2WithHmacSHA256"
        const val KeyAlgorithm = "AES"
        const val KeySizeBits = 256
        const val AuthenticationTagSizeBits = 128
        const val SaltSizeBytes = 16
        const val NonceSizeBytes = 12
    }
}
