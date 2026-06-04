package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupDecryptResult
import com.jeong.sumdiary.domain.backup.BackupPassphrase
import com.jeong.sumdiary.domain.backup.BackupSnapshot
import com.jeong.sumdiary.domain.backup.BackupSnapshotSettings
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.summary.Summary
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

@OptIn(ExperimentalEncodingApi::class)
class RealBackupEncryptorTest {
    private val passphrase = BackupPassphrase("safe backup passphrase")
    private val snapshot = BackupSnapshot(
        createdAtEpochMillis = 1234L,
        entries = listOf(
            DiaryEntry(
                id = "entry-1",
                date = LocalDate(2026, 6, 4),
                time = LocalTime(9, 30),
                content = "오늘은 백업 암호화를 구현했다."
            )
        ),
        summaries = listOf(
            Summary(
                type = SummaryType.DAILY,
                periodStart = LocalDate(2026, 6, 4),
                periodEnd = LocalDate(2026, 6, 4),
                text = "백업 암호화 구현",
                emotions = listOf("차분함")
            )
        ),
        settings = BackupSnapshotSettings(
            aiSummaryEnabled = true,
            backupEnabled = true
        )
    )

    @Test
    fun encryptStoresBase64CipherTextInsteadOfPlainPayloadReference() = runTest {
        val encryptor = RealBackupEncryptor(TestBackupCipher())

        val file = encryptor.encrypt(snapshot, passphrase)

        requireNotNull(file)
        assertFalse(file.encryptedPayloadBase64.startsWith("fake-encrypted"))
        assertFalse(Base64.decode(file.encryptedPayloadBase64).decodeToString().contains(snapshot.entries.first().content))
    }

    @Test
    fun decryptReturnsSnapshotWithCorrectPassphrase() = runTest {
        val encryptor = RealBackupEncryptor(TestBackupCipher())
        val file = requireNotNull(encryptor.encrypt(snapshot, passphrase))

        val result = encryptor.decrypt(file, passphrase)

        val success = assertIs<BackupDecryptResult.Success>(result)
        assertEquals(snapshot, success.snapshot)
    }

    @Test
    fun decryptReturnsInvalidPassphraseWithWrongPassphrase() = runTest {
        val encryptor = RealBackupEncryptor(TestBackupCipher())
        val file = requireNotNull(encryptor.encrypt(snapshot, passphrase))

        val result = encryptor.decrypt(file, BackupPassphrase("wrong backup passphrase"))

        assertEquals(BackupDecryptResult.InvalidPassphrase, result)
    }

    @Test
    fun decryptReturnsCorruptedFileWithMalformedPayload() = runTest {
        val encryptor = RealBackupEncryptor(TestBackupCipher())
        val file = requireNotNull(encryptor.encrypt(snapshot, passphrase))
            .copy(encryptedPayloadBase64 = "not-base64")

        val result = encryptor.decrypt(file, passphrase)

        assertEquals(BackupDecryptResult.CorruptedFile, result)
    }

    private fun runTest(block: suspend () -> Unit) {
        kotlinx.coroutines.test.runTest {
            block()
        }
    }
}

private class TestBackupCipher : BackupCipher {
    override val algorithm: String = "TEST_AES_256_GCM"
    override val keyDerivation: String = "TEST_PBKDF2_HMAC_SHA256"
    override val iterations: Int = 1

    override fun generateSalt(): ByteArray = "test-salt".encodeToByteArray()
    override fun generateNonce(): ByteArray = "test-nonce".encodeToByteArray()

    override fun encrypt(
        plainBytes: ByteArray,
        passphrase: BackupPassphrase,
        salt: ByteArray,
        nonce: ByteArray
    ): ByteArray = xor(plainBytes, passphrase)

    override fun decrypt(
        encryptedBytes: ByteArray,
        passphrase: BackupPassphrase,
        salt: ByteArray,
        nonce: ByteArray
    ): ByteArray? {
        if (passphrase.value != "safe backup passphrase") return null
        return xor(encryptedBytes, passphrase)
    }

    private fun xor(
        bytes: ByteArray,
        passphrase: BackupPassphrase
    ): ByteArray {
        val key = passphrase.value.encodeToByteArray()
        return ByteArray(bytes.size) { index ->
            (bytes[index].toInt() xor key[index % key.size].toInt()).toByte()
        }
    }
}
