package com.jeong.sumdiary.feature.backup

import com.jeong.sumdiary.domain.backup.BackupCloudConnectionResult
import com.jeong.sumdiary.domain.backup.BackupCloudRepository
import com.jeong.sumdiary.domain.backup.BackupCloudSession
import com.jeong.sumdiary.domain.backup.BackupCloudSessionState
import com.jeong.sumdiary.domain.backup.BackupDeleteResult
import com.jeong.sumdiary.domain.backup.BackupDownloadResult
import com.jeong.sumdiary.domain.backup.BackupPassphrase
import com.jeong.sumdiary.domain.backup.BackupProvider
import com.jeong.sumdiary.domain.backup.BackupRepository
import com.jeong.sumdiary.domain.backup.BackupRestoreMode
import com.jeong.sumdiary.domain.backup.BackupRestoreResult
import com.jeong.sumdiary.domain.backup.BackupRunResult
import com.jeong.sumdiary.domain.backup.BackupUploadResult
import com.jeong.sumdiary.domain.backup.EncryptedBackupFile
import com.jeong.sumdiary.domain.backup.usecase.DeleteRemoteBackup
import com.jeong.sumdiary.domain.backup.usecase.RestoreBackup
import com.jeong.sumdiary.domain.backup.usecase.RunBackup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BackupViewModelTest {
    @Test
    fun initialStateExplainsBackupDeletionDoesNotRevokeGoogleAccountPermission() {
        val viewModel = viewModel()

        assertEquals(BackupState.BackupAccountRemovalNotice, viewModel.state.value.accountRemovalNotice)
        assertTrue(viewModel.state.value.accountRemovalNotice.contains("Google 계정 권한 해제"))
    }

    @Test
    fun deleteRemoteBackupSuccessKeepsProviderAccountRevocationSeparate() {
        val viewModel = viewModel(
            backupRepository = FakeBackupRepository(deleteResult = BackupDeleteResult.Success(BackupProvider.GoogleDrive)),
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.dispatch(BackupIntent.DeleteRemoteBackup)

        val state = viewModel.state.value
        assertEquals(BackupUiStatus.Success, state.status)
        assertTrue(state.statusDescription.contains("SumDiary 백업 파일"))
        assertTrue(state.statusDescription.contains("Google 계정 권한"))
        assertEquals(BackupState.BackupAccountRemovalNotice, state.accountRemovalNotice)
    }

    private fun viewModel(
        backupRepository: BackupRepository = FakeBackupRepository(),
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
    ): BackupViewModel =
        BackupViewModel(
            provider = BackupProvider.GoogleDrive,
            cloudRepository = FakeBackupCloudRepository(),
            runBackup = RunBackup(backupRepository),
            restoreBackup = RestoreBackup(backupRepository),
            deleteRemoteBackup = DeleteRemoteBackup(backupRepository),
            dispatcher = dispatcher
        )

    private class FakeBackupRepository(
        private val deleteResult: BackupDeleteResult = BackupDeleteResult.FileNotFound
    ) : BackupRepository {
        override suspend fun runBackup(passphrase: BackupPassphrase): BackupRunResult =
            BackupRunResult.NotConnected

        override suspend fun restoreBackup(
            passphrase: BackupPassphrase,
            mode: BackupRestoreMode
        ): BackupRestoreResult = BackupRestoreResult.NotConnected

        override suspend fun deleteRemoteBackup(): BackupDeleteResult = deleteResult
    }

    private class FakeBackupCloudRepository : BackupCloudRepository {
        override val provider: BackupProvider = BackupProvider.GoogleDrive

        override suspend fun connect(): BackupCloudConnectionResult =
            BackupCloudConnectionResult.Cancelled

        override suspend fun currentSession(): BackupCloudSession =
            BackupCloudSession(
                provider = provider,
                state = BackupCloudSessionState.NotConnected
            )

        override suspend fun upload(file: EncryptedBackupFile): BackupUploadResult =
            BackupUploadResult.NotConnected

        override suspend fun downloadLatest(): BackupDownloadResult =
            BackupDownloadResult.NotConnected

        override suspend fun deleteRemoteFile(): BackupDeleteResult =
            BackupDeleteResult.NotConnected
    }
}
