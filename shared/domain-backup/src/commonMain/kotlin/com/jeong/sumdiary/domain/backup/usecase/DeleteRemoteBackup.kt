package com.jeong.sumdiary.domain.backup.usecase

import com.jeong.sumdiary.domain.backup.BackupDeleteResult
import com.jeong.sumdiary.domain.backup.BackupRepository

class DeleteRemoteBackup(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(): BackupDeleteResult =
        backupRepository.deleteRemoteBackup()
}
