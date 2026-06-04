package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupCloudRepository
import com.jeong.sumdiary.domain.backup.BackupCloudSession
import com.jeong.sumdiary.domain.backup.BackupDecryptResult
import com.jeong.sumdiary.domain.backup.BackupDeleteResult
import com.jeong.sumdiary.domain.backup.BackupDownloadResult
import com.jeong.sumdiary.domain.backup.BackupEncryptor
import com.jeong.sumdiary.domain.backup.BackupPassphrase
import com.jeong.sumdiary.domain.backup.BackupRepository
import com.jeong.sumdiary.domain.backup.BackupRestoreMode
import com.jeong.sumdiary.domain.backup.BackupRestoreResult
import com.jeong.sumdiary.domain.backup.BackupRunResult
import com.jeong.sumdiary.domain.backup.BackupSnapshotRepository
import com.jeong.sumdiary.domain.backup.BackupUploadResult

class BackupRepositoryImpl(
    private val snapshotRepository: BackupSnapshotRepository,
    private val backupEncryptor: BackupEncryptor,
    private val cloudRepository: BackupCloudRepository
) : BackupRepository {
    override suspend fun runBackup(passphrase: BackupPassphrase): BackupRunResult {
        if (!cloudRepository.currentSession().canAccessBackupFile()) {
            return BackupRunResult.NotConnected
        }

        val snapshot = snapshotRepository.buildSnapshot()
        val encryptedFile = backupEncryptor.encrypt(
            snapshot = snapshot,
            passphrase = passphrase
        ) ?: return BackupRunResult.EncryptionFailed

        return when (val result = cloudRepository.upload(encryptedFile)) {
            is BackupUploadResult.Success -> BackupRunResult.Success(
                provider = result.provider,
                fileName = result.fileName,
                uploadedAtEpochMillis = result.uploadedAtEpochMillis
            )
            BackupUploadResult.NotConnected -> BackupRunResult.NotConnected
            BackupUploadResult.Failed -> BackupRunResult.UploadFailed
        }
    }

    override suspend fun restoreBackup(
        passphrase: BackupPassphrase,
        mode: BackupRestoreMode
    ): BackupRestoreResult {
        if (!cloudRepository.currentSession().canAccessBackupFile()) {
            return BackupRestoreResult.NotConnected
        }

        val encryptedFile = when (val result = cloudRepository.downloadLatest()) {
            is BackupDownloadResult.Success -> result.file
            BackupDownloadResult.NotConnected -> return BackupRestoreResult.NotConnected
            BackupDownloadResult.FileNotFound -> return BackupRestoreResult.FileNotFound
            BackupDownloadResult.Failed -> return BackupRestoreResult.CorruptedFile
        }

        val snapshot = when (val result = backupEncryptor.decrypt(encryptedFile, passphrase)) {
            is BackupDecryptResult.Success -> result.snapshot
            BackupDecryptResult.InvalidPassphrase -> return BackupRestoreResult.InvalidPassphrase
            BackupDecryptResult.UnsupportedVersion -> return BackupRestoreResult.UnsupportedVersion
            BackupDecryptResult.CorruptedFile -> return BackupRestoreResult.CorruptedFile
        }

        val report = snapshotRepository.restoreSnapshot(
            snapshot = snapshot,
            mode = mode
        )
        return BackupRestoreResult.Success(
            provider = cloudRepository.provider,
            restoredEntries = report.restoredEntries,
            restoredSummaries = report.restoredSummaries
        )
    }

    override suspend fun deleteRemoteBackup(): BackupDeleteResult =
        cloudRepository.deleteRemoteFile()

    private fun BackupCloudSession.canAccessBackupFile(): Boolean =
        isConnected && hasRequiredScope
}
