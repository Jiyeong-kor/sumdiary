package com.jeong.sumdiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
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
                SumDiaryScreen(container)
            }
        }
    }
}
