package com.jeong.sumdiary.feature.backup

import com.jeong.sumdiary.domain.backup.BackupDeleteResult
import com.jeong.sumdiary.domain.backup.BackupPassphrase
import com.jeong.sumdiary.domain.backup.BackupProvider
import com.jeong.sumdiary.domain.backup.BackupRestoreMode
import com.jeong.sumdiary.domain.backup.BackupRestoreResult
import com.jeong.sumdiary.domain.backup.BackupRunResult
import com.jeong.sumdiary.domain.backup.usecase.DeleteRemoteBackup
import com.jeong.sumdiary.domain.backup.usecase.RestoreBackup
import com.jeong.sumdiary.domain.backup.usecase.RunBackup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackupViewModel(
    provider: BackupProvider,
    private val runBackup: RunBackup,
    private val restoreBackup: RestoreBackup,
    private val deleteRemoteBackup: DeleteRemoteBackup,
    private val connectProviderForDevelopment: suspend () -> Unit,
    private val dispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val developmentPassphrase = BackupPassphrase("development-only-passphrase")
    private val _state = MutableStateFlow(BackupState.initial(provider.displayName))
    val state: StateFlow<BackupState> = _state.asStateFlow()

    fun dispatch(intent: BackupIntent) {
        when (intent) {
            BackupIntent.ConnectGoogleDrive -> connect()
            BackupIntent.RunManualBackup -> runManualBackup()
            BackupIntent.RestoreMerge -> restoreMerge()
            BackupIntent.DeleteRemoteBackup -> deleteRemote()
        }
    }

    private fun connect() {
        scope.launch {
            _state.value = _state.value.toRunning("Google Drive 연결 확인 중")
            runCatching { connectProviderForDevelopment() }
                .onSuccess {
                    _state.value = _state.value.copy(
                        connected = true,
                        status = BackupUiStatus.Ready,
                        statusTitle = "Google Drive 준비됨",
                        statusDescription = "암호화된 백업 파일만 저장할 수 있는 상태예요.",
                        lastResult = "연결 준비가 완료됐어요.",
                        busy = false
                    )
                }
                .onFailure {
                    _state.value = _state.value.toFailure("Google Drive 연결을 확인하지 못했어요.")
                }
        }
    }

    private fun runManualBackup() {
        scope.launch {
            _state.value = _state.value.toRunning("백업 암호화 준비 중")
            val result = runCatching { runBackup(developmentPassphrase) }.getOrElse {
                _state.value = _state.value.toFailure("백업을 완료하지 못했어요.")
                return@launch
            }
            _state.value = result.toState(_state.value.providerName)
        }
    }

    private fun restoreMerge() {
        scope.launch {
            _state.value = _state.value.toRunning("복구 파일 확인 중")
            val result = runCatching {
                restoreBackup(
                    passphrase = developmentPassphrase,
                    mode = BackupRestoreMode.Merge
                )
            }.getOrElse {
                _state.value = _state.value.toFailure("복구를 완료하지 못했어요.")
                return@launch
            }
            _state.value = result.toState(_state.value.providerName)
        }
    }

    private fun deleteRemote() {
        scope.launch {
            _state.value = _state.value.toRunning("원격 백업 파일 삭제 중")
            val result = runCatching { deleteRemoteBackup() }.getOrElse {
                _state.value = _state.value.toFailure("원격 백업 파일을 삭제하지 못했어요.")
                return@launch
            }
            _state.value = result.toState(_state.value.providerName)
        }
    }

    private fun BackupState.toRunning(title: String): BackupState = copy(
        status = BackupUiStatus.Running,
        statusTitle = title,
        statusDescription = "민감한 데이터는 화면이나 로그에 표시하지 않아요.",
        busy = true
    )

    private fun BackupState.toFailure(message: String): BackupState = copy(
        status = BackupUiStatus.Failed,
        statusTitle = "작업 실패",
        statusDescription = "잠시 후 다시 시도해 주세요.",
        lastResult = message,
        busy = false
    )

    private fun BackupRunResult.toState(providerName: String): BackupState =
        when (this) {
            is BackupRunResult.Success -> BackupState(
                providerName = providerName,
                connected = true,
                status = BackupUiStatus.Success,
                statusTitle = "백업 완료",
                statusDescription = "${providerName}에 암호화된 백업 파일을 저장했어요.",
                lastResult = fileName,
                busy = false
            )
            BackupRunResult.NotConnected -> needsConnection(providerName)
            BackupRunResult.EncryptionFailed -> failed(providerName, "백업 파일을 암호화하지 못했어요.")
            BackupRunResult.UploadFailed -> failed(providerName, "백업 파일을 업로드하지 못했어요.")
        }

    private fun BackupRestoreResult.toState(providerName: String): BackupState =
        when (this) {
            is BackupRestoreResult.Success -> BackupState(
                providerName = providerName,
                connected = true,
                status = BackupUiStatus.Success,
                statusTitle = "복구 완료",
                statusDescription = "백업 데이터를 현재 기기 데이터와 병합했어요.",
                lastResult = "일기 ${restoredEntries}개 · 요약 ${restoredSummaries}개",
                busy = false
            )
            BackupRestoreResult.NotConnected -> needsConnection(providerName)
            BackupRestoreResult.FileNotFound -> failed(providerName, "백업 파일을 찾지 못했어요.")
            BackupRestoreResult.InvalidPassphrase -> failed(providerName, "백업 비밀번호가 맞지 않아요.")
            BackupRestoreResult.UnsupportedVersion -> failed(providerName, "지원하지 않는 백업 파일이에요.")
            BackupRestoreResult.CorruptedFile -> failed(providerName, "백업 파일을 읽지 못했어요.")
        }

    private fun BackupDeleteResult.toState(providerName: String): BackupState =
        when (this) {
            is BackupDeleteResult.Success -> BackupState(
                providerName = providerName,
                connected = true,
                status = BackupUiStatus.Success,
                statusTitle = "원격 백업 삭제 완료",
                statusDescription = "${providerName}의 SumDiary 백업 파일을 삭제했어요.",
                lastResult = null,
                busy = false
            )
            BackupDeleteResult.NotConnected -> needsConnection(providerName)
            BackupDeleteResult.FileNotFound -> failed(providerName, "삭제할 백업 파일이 없어요.")
            BackupDeleteResult.DeleteFailed -> failed(providerName, "원격 백업 파일을 삭제하지 못했어요.")
        }

    private fun needsConnection(providerName: String): BackupState = BackupState(
        providerName = providerName,
        connected = false,
        status = BackupUiStatus.NeedsConnection,
        statusTitle = "$providerName 연결 필요",
        statusDescription = "먼저 사용자가 직접 Google Drive 연결을 시작해야 해요.",
        lastResult = null,
        busy = false
    )

    private fun failed(providerName: String, message: String): BackupState = BackupState(
        providerName = providerName,
        connected = true,
        status = BackupUiStatus.Failed,
        statusTitle = "작업 실패",
        statusDescription = "원본 로컬 데이터는 그대로 보존돼요.",
        lastResult = message,
        busy = false
    )
}
