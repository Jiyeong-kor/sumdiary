package com.jeong.sumdiary.domain.backup.usecase

import com.jeong.sumdiary.domain.backup.BackupPassphrase
import com.jeong.sumdiary.domain.backup.BackupRepository
import com.jeong.sumdiary.domain.backup.BackupRestoreMode
import com.jeong.sumdiary.domain.backup.BackupRestoreResult

class RestoreBackup(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(
        passphrase: BackupPassphrase,
        mode: BackupRestoreMode
    ): BackupRestoreResult = backupRepository.restoreBackup(
        passphrase = passphrase,
        mode = mode
    )
}
