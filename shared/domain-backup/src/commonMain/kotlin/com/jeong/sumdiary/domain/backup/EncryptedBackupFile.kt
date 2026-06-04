package com.jeong.sumdiary.domain.backup

data class EncryptedBackupFile(
    val format: String = BackupConstants.CurrentFormat,
    val version: Int = BackupConstants.CurrentVersion,
    val fileName: String = BackupConstants.CurrentFileName,
    val provider: BackupProvider = BackupProvider.GoogleDrive,
    val createdAtEpochMillis: Long,
    val encryption: BackupEncryptionMetadata,
    val encryptedPayloadBase64: String
)

data class BackupEncryptionMetadata(
    val algorithm: String,
    val keyDerivation: String,
    val saltBase64: String,
    val nonceBase64: String,
    val iterations: Int
)
