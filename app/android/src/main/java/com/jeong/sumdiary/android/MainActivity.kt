package com.jeong.sumdiary.android

import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.jeong.sumdiary.android.backup.AndroidGoogleDriveAccessTokenProvider
import com.jeong.sumdiary.android.di.AppContainer
import com.jeong.sumdiary.android.ui.SumDiaryScreen
import com.jeong.sumdiary.core.designsystem.SumDiaryTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var container: AppContainer
    private lateinit var googleDriveAccessTokenProvider: AndroidGoogleDriveAccessTokenProvider
    private val googleDriveAuthorizationLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        googleDriveAccessTokenProvider.handleAuthorizationResult(result.data)
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

    private fun isAppLockAvailable(): Boolean {
        val authenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        return BiometricManager.from(this).canAuthenticate(authenticators) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun requestAppUnlock(onResult: (success: Boolean, message: String?) -> Unit) {
        val authenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        if (!isAppLockAvailable()) {
            onResult(false, "이 기기에는 사용할 수 있는 생체 또는 화면 잠금 인증이 없어요.")
            return
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("SumDiary 잠금 해제")
            .setSubtitle("일기와 백업 설정을 보려면 OS 인증이 필요해요.")
            .setAllowedAuthenticators(authenticators)
            .build()
        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    onResult(true, null)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onResult(false, errString.toString())
                }

                override fun onAuthenticationFailed() {
                    onResult(false, "인증하지 못했어요. 다시 시도해 주세요.")
                }
            }
        )
        prompt.authenticate(promptInfo)
    }
}
