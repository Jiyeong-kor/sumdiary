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

    @Test
    fun connectGoogleDriveSuccessMarksProviderReady() {
        val viewModel = viewModel(
            cloudRepository = FakeBackupCloudRepository(
                connectionResult = BackupCloudConnectionResult.Connected(
                    BackupCloudSession(
                        provider = BackupProvider.GoogleDrive,
                        state = BackupCloudSessionState.Connected
                    )
                )
            ),
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.dispatch(BackupIntent.ConnectGoogleDrive)

        val state = viewModel.state.value
        assertEquals(BackupUiStatus.Ready, state.status)
        assertTrue(state.connected)
        assertEquals("필수 권한 확인 완료", state.lastResult)
        assertTrue(state.statusDescription.contains("암호화된 백업 파일"))
    }

    @Test
    fun connectGoogleDriveMissingScopeRequestsAppDataPermission() {
        val viewModel = viewModel(
            cloudRepository = FakeBackupCloudRepository(
                connectionResult = BackupCloudConnectionResult.MissingRequiredScope(
                    BackupCloudSession(
                        provider = BackupProvider.GoogleDrive,
                        state = BackupCloudSessionState.Connected
                    )
                )
            ),
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.dispatch(BackupIntent.ConnectGoogleDrive)

        val state = viewModel.state.value
        assertEquals(BackupUiStatus.NeedsConnection, state.status)
        assertTrue(state.connected)
        assertTrue(state.statusTitle.contains("권한 필요"))
        assertTrue(state.statusDescription.contains("앱 전용 Drive 권한"))
    }

    @Test
    fun runManualBackupSuccessShowsEncryptedGoogleDriveFile() {
        val viewModel = viewModel(
            backupRepository = FakeBackupRepository(
                runResult = BackupRunResult.Success(
                    provider = BackupProvider.GoogleDrive,
                    fileName = "sumdiary-backup-2026-06-05.sdb",
                    uploadedAtEpochMillis = 1_780_000_000_000
                )
            ),
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.dispatch(BackupIntent.RunManualBackup(passphrase()))

        val state = viewModel.state.value
        assertEquals(BackupUiStatus.Success, state.status)
        assertTrue(state.connected)
        assertEquals("백업 완료", state.statusTitle)
        assertTrue(state.statusDescription.contains("암호화된 백업 파일"))
        assertEquals("sumdiary-backup-2026-06-05.sdb", state.lastResult)
    }

    @Test
    fun restoreMergeSuccessShowsRestoredCounts() {
        val viewModel = viewModel(
            backupRepository = FakeBackupRepository(
                restoreResult = BackupRestoreResult.Success(
                    provider = BackupProvider.GoogleDrive,
                    restoredEntries = 7,
                    restoredSummaries = 2
                )
            ),
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.dispatch(BackupIntent.RestoreMerge(passphrase()))

        val state = viewModel.state.value
        assertEquals(BackupUiStatus.Success, state.status)
        assertTrue(state.connected)
        assertEquals("복구 완료", state.statusTitle)
        assertEquals("일기 7개 · 요약 2개", state.lastResult)
    }

    @Test
    fun deleteRemoteBackupFileNotFoundKeepsLocalDataSafeMessage() {
        val viewModel = viewModel(
            backupRepository = FakeBackupRepository(deleteResult = BackupDeleteResult.FileNotFound),
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.dispatch(BackupIntent.DeleteRemoteBackup)

        val state = viewModel.state.value
        assertEquals(BackupUiStatus.Failed, state.status)
        assertTrue(state.connected)
        assertEquals("삭제할 백업 파일이 없어요.", state.lastResult)
        assertTrue(state.statusDescription.contains("원본 로컬 데이터"))
    }

    private fun viewModel(
        backupRepository: BackupRepository = FakeBackupRepository(),
        cloudRepository: BackupCloudRepository = FakeBackupCloudRepository(),
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
    ): BackupViewModel =
        BackupViewModel(
            provider = BackupProvider.GoogleDrive,
            cloudRepository = cloudRepository,
            runBackup = RunBackup(backupRepository),
            restoreBackup = RestoreBackup(backupRepository),
            deleteRemoteBackup = DeleteRemoteBackup(backupRepository),
            dispatcher = dispatcher
        )

    private class FakeBackupRepository(
        private val runResult: BackupRunResult = BackupRunResult.NotConnected,
        private val restoreResult: BackupRestoreResult = BackupRestoreResult.NotConnected,
        private val deleteResult: BackupDeleteResult = BackupDeleteResult.FileNotFound
    ) : BackupRepository {
        override suspend fun runBackup(passphrase: BackupPassphrase): BackupRunResult =
            runResult

        override suspend fun restoreBackup(
            passphrase: BackupPassphrase,
            mode: BackupRestoreMode
        ): BackupRestoreResult = restoreResult

        override suspend fun deleteRemoteBackup(): BackupDeleteResult = deleteResult
    }

    private class FakeBackupCloudRepository(
        private val connectionResult: BackupCloudConnectionResult = BackupCloudConnectionResult.Cancelled
    ) : BackupCloudRepository {
        override val provider: BackupProvider = BackupProvider.GoogleDrive

        override suspend fun connect(): BackupCloudConnectionResult =
            connectionResult

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

    private fun passphrase(): BackupPassphrase =
        BackupPassphrase("release-ready-passphrase")
}
