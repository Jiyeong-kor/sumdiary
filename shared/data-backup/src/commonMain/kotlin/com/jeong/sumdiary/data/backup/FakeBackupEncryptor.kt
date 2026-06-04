package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupConstants
import com.jeong.sumdiary.domain.backup.BackupDecryptResult
import com.jeong.sumdiary.domain.backup.BackupEncryptionMetadata
import com.jeong.sumdiary.domain.backup.BackupEncryptor
import com.jeong.sumdiary.domain.backup.BackupPassphrase
import com.jeong.sumdiary.domain.backup.BackupProvider
import com.jeong.sumdiary.domain.backup.BackupSnapshot
import com.jeong.sumdiary.domain.backup.EncryptedBackupFile

class FakeBackupEncryptor(
    private val provider: BackupProvider = BackupProvider.GoogleDrive
) : BackupEncryptor {
    private val encryptedSnapshots = mutableMapOf<String, BackupSnapshot>()
    private val passphraseHashes = mutableMapOf<String, Int>()

    override suspend fun encrypt(
        snapshot: BackupSnapshot,
        passphrase: BackupPassphrase
    ): EncryptedBackupFile {
        val payloadId = "fake-encrypted-${snapshot.createdAtEpochMillis}"
        encryptedSnapshots[payloadId] = snapshot
        passphraseHashes[payloadId] = passphrase.value.hashCode()

        return EncryptedBackupFile(
            provider = provider,
            createdAtEpochMillis = snapshot.createdAtEpochMillis,
            encryption = BackupEncryptionMetadata(
                algorithm = "FAKE_AES_256_GCM_DEV_ONLY",
                keyDerivation = "FAKE_PBKDF2_DEV_ONLY",
                saltBase64 = "dev-only-salt",
                nonceBase64 = "dev-only-nonce",
                iterations = 1
            ),
            encryptedPayloadBase64 = payloadId
        )
    }

    override suspend fun decrypt(
        file: EncryptedBackupFile,
        passphrase: BackupPassphrase
    ): BackupDecryptResult {
        if (file.format != BackupConstants.CurrentFormat || file.version > BackupConstants.CurrentVersion) {
            return BackupDecryptResult.UnsupportedVersion
        }
        if (passphraseHashes[file.encryptedPayloadBase64] != passphrase.value.hashCode()) {
            return BackupDecryptResult.InvalidPassphrase
        }
        return encryptedSnapshots[file.encryptedPayloadBase64]
            ?.let(BackupDecryptResult::Success)
            ?: BackupDecryptResult.CorruptedFile
    }
}
