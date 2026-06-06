package com.jeong.sumdiary.android

import android.app.Activity
import android.app.KeyguardManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.jeong.sumdiary.android.backup.AndroidGoogleDriveAccessTokenProvider
import com.jeong.sumdiary.android.di.AppContainer
import com.jeong.sumdiary.android.ui.SumDiaryScreen
import com.jeong.sumdiary.core.designsystem.SumDiaryTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var container: AppContainer
    private lateinit var googleDriveAccessTokenProvider: AndroidGoogleDriveAccessTokenProvider
    private var pendingAppUnlockResult: ((success: Boolean, message: String?) -> Unit)? = null
    private val keyguardManager: KeyguardManager by lazy {
        getSystemService(KeyguardManager::class.java)
    }
    private val googleDriveAuthorizationLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        googleDriveAccessTokenProvider.handleAuthorizationResult(result.data)
    }
    private val appUnlockLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val onResult = pendingAppUnlockResult ?: return@registerForActivityResult
        pendingAppUnlockResult = null
        if (result.resultCode == Activity.RESULT_OK) {
            onResult(true, null)
        } else {
            onResult(false, "인증하지 못했어요. 다시 시도해 주세요.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleDriveAccessTokenProvider = AndroidGoogleDriveAccessTokenProvider(
            activity = this,
            authorizationLauncher = googleDriveAuthorizationLauncher
        )
        container.setGoogleDriveAccessTokenProvider(googleDriveAccessTokenProvider)
        setContent {
            SumDiaryTheme {
                SumDiaryScreen(
                    container = container,
                    appLockAvailable = isAppLockAvailable(),
                    onRequestAppUnlock = ::requestAppUnlock
                )
            }
        }
    }

    private fun isAppLockAvailable(): Boolean = keyguardManager.isDeviceSecure

    @Suppress("DEPRECATION")
    private fun requestAppUnlock(onResult: (success: Boolean, message: String?) -> Unit) {
        if (!isAppLockAvailable()) {
            onResult(false, "이 기기에는 사용할 수 있는 생체 또는 화면 잠금 인증이 없어요.")
            return
        }

        val intent = keyguardManager.createConfirmDeviceCredentialIntent(
            "SumDiary 잠금 해제",
            "일기와 백업 설정을 보려면 OS 인증이 필요해요."
        )
        if (intent == null) {
            onResult(false, "이 기기에는 사용할 수 있는 생체 또는 화면 잠금 인증이 없어요.")
            return
        }

        pendingAppUnlockResult = onResult
        appUnlockLauncher.launch(intent)
    }
}
