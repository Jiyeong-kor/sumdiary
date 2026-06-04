package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupConstants
import com.jeong.sumdiary.domain.backup.BackupDecryptResult
import com.jeong.sumdiary.domain.backup.BackupEncryptionMetadata
import com.jeong.sumdiary.domain.backup.BackupEncryptor
import com.jeong.sumdiary.domain.backup.BackupPassphrase
import com.jeong.sumdiary.domain.backup.BackupProvider
import com.jeong.sumdiary.domain.backup.BackupSnapshot
import com.jeong.sumdiary.domain.backup.EncryptedBackupFile
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class RealBackupEncryptor(
    private val cipher: BackupCipher,
    private val codec: BackupPayloadCodec = BackupPayloadCodec(),
    private val provider: BackupProvider = BackupProvider.GoogleDrive
) : BackupEncryptor {
    override suspend fun encrypt(
        snapshot: BackupSnapshot,
        passphrase: BackupPassphrase
    ): EncryptedBackupFile? {
        val salt = cipher.generateSalt()
        val nonce = cipher.generateNonce()
        val encryptedPayload = cipher.encrypt(
            plainBytes = codec.encode(snapshot),
            passphrase = passphrase,
            salt = salt,
            nonce = nonce
        ) ?: return null

        return EncryptedBackupFile(
            provider = provider,
            createdAtEpochMillis = snapshot.createdAtEpochMillis,
            encryption = BackupEncryptionMetadata(
                algorithm = cipher.algorithm,
                keyDerivation = cipher.keyDerivation,
                saltBase64 = Base64.encode(salt),
                nonceBase64 = Base64.encode(nonce),
                iterations = cipher.iterations
            ),
            encryptedPayloadBase64 = Base64.encode(encryptedPayload)
        )
    }

    override suspend fun decrypt(
        file: EncryptedBackupFile,
        passphrase: BackupPassphrase
    ): BackupDecryptResult {
        if (file.format != BackupConstants.CurrentFormat || file.version > BackupConstants.CurrentVersion) {
            return BackupDecryptResult.UnsupportedVersion
        }
        if (file.encryption.algorithm != cipher.algorithm ||
            file.encryption.keyDerivation != cipher.keyDerivation ||
            file.encryption.iterations != cipher.iterations
        ) {
            return BackupDecryptResult.UnsupportedVersion
        }

        val salt = file.encryption.saltBase64.decodeBase64OrNull() ?: return BackupDecryptResult.CorruptedFile
        val nonce = file.encryption.nonceBase64.decodeBase64OrNull() ?: return BackupDecryptResult.CorruptedFile
        val encryptedPayload = file.encryptedPayloadBase64.decodeBase64OrNull()
            ?: return BackupDecryptResult.CorruptedFile

        val plainBytes = cipher.decrypt(
            encryptedBytes = encryptedPayload,
            passphrase = passphrase,
            salt = salt,
            nonce = nonce
        ) ?: return BackupDecryptResult.InvalidPassphrase

        return runCatching { BackupDecryptResult.Success(codec.decode(plainBytes)) }
            .getOrElse { BackupDecryptResult.CorruptedFile }
    }

    private fun String.decodeBase64OrNull(): ByteArray? =
        runCatching { Base64.decode(this) }.getOrNull()
}
