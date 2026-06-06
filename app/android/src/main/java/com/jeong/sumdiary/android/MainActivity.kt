package com.jeong.sumdiary.android

import android.app.Activity
import android.app.KeyguardManager
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
    private var appUnlockCancellationSignal: CancellationSignal? = null
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
        if (result.resultCode == Activity.RESULT_OK) {
            completeAppUnlock(success = true, message = null)
        } else {
            completeAppUnlock(success = false, message = "인증하지 못했어요. 다시 시도해 주세요.")
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

    override fun onDestroy() {
        appUnlockCancellationSignal?.cancel()
        appUnlockCancellationSignal = null
        super.onDestroy()
    }

    private fun isAppLockAvailable(): Boolean =
        isBiometricAuthenticationAvailable() || isDeviceCredentialAvailable()

    private fun requestAppUnlock(onResult: (success: Boolean, message: String?) -> Unit) {
        if (!isAppLockAvailable()) {
            onResult(false, "이 기기에는 사용할 수 있는 생체 또는 화면 잠금 인증이 없어요.")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && isBiometricAuthenticationAvailable()) {
            requestBiometricUnlock(onResult)
        } else {
            requestDeviceCredentialUnlock(onResult)
        }
    }

    private fun isDeviceCredentialAvailable(): Boolean = keyguardManager.isDeviceSecure

    private fun isBiometricAuthenticationAvailable(): Boolean =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                val biometricManager = getSystemService(BiometricManager::class.java)
                biometricManager.canAuthenticate(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
                ) == BiometricManager.BIOMETRIC_SUCCESS
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                val biometricManager = getSystemService(BiometricManager::class.java)
                @Suppress("DEPRECATION")
                biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
            }
            else -> isLegacyFingerprintAuthenticationAvailable()
        }

    @Suppress("DEPRECATION")
    private fun isLegacyFingerprintAuthenticationAvailable(): Boolean {
        val fingerprintManager = getSystemService(
            android.hardware.fingerprint.FingerprintManager::class.java
        )
        return fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun requestBiometricUnlock(onResult: (success: Boolean, message: String?) -> Unit) {
        pendingAppUnlockResult = onResult
        val cancellationSignal = CancellationSignal()
        appUnlockCancellationSignal = cancellationSignal
        createBiometricPrompt().authenticate(
            cancellationSignal,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    completeAppUnlock(success = true, message = null)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    completeAppUnlock(success = false, message = errString.toString())
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun createBiometricPrompt(): BiometricPrompt {
        val builder = BiometricPrompt.Builder(this)
            .setTitle("SumDiary 잠금 해제")
            .setSubtitle("일기와 백업 설정을 보려면 OS 인증이 필요해요.")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            @Suppress("DEPRECATION")
            builder.setDeviceCredentialAllowed(isDeviceCredentialAvailable())
        }

        return builder.build()
    }

    @Suppress("DEPRECATION")
    private fun requestDeviceCredentialUnlock(onResult: (success: Boolean, message: String?) -> Unit) {
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

    private fun completeAppUnlock(success: Boolean, message: String?) {
        val onResult = pendingAppUnlockResult ?: return
        pendingAppUnlockResult = null
        appUnlockCancellationSignal = null
        onResult(success, message)
    }
}
