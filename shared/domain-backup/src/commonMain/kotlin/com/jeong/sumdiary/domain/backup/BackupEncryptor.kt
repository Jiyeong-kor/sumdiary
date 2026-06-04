package com.jeong.sumdiary.domain.backup

sealed interface BackupDecryptResult {
    data class Success(val snapshot: BackupSnapshot) : BackupDecryptResult
    data object InvalidPassphrase : BackupDecryptResult
    data object UnsupportedVersion : BackupDecryptResult
    data object CorruptedFile : BackupDecryptResult
}

interface BackupEncryptor {
    suspend fun encrypt(
        snapshot: BackupSnapshot,
        passphrase: BackupPassphrase
    ): EncryptedBackupFile?

    suspend fun decrypt(
        file: EncryptedBackupFile,
        passphrase: BackupPassphrase
    ): BackupDecryptResult
}
