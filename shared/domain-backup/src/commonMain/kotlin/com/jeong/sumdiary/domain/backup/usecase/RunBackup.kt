package com.jeong.sumdiary.domain.backup.usecase

import com.jeong.sumdiary.domain.backup.BackupPassphrase
import com.jeong.sumdiary.domain.backup.BackupRepository
import com.jeong.sumdiary.domain.backup.BackupRunResult

class RunBackup(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(passphrase: BackupPassphrase): BackupRunResult =
        backupRepository.runBackup(passphrase)
}
