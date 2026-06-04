package com.jeong.sumdiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jeong.sumdiary.android.di.AppContainer
import com.jeong.sumdiary.android.ui.SumDiaryScreen
import com.jeong.sumdiary.core.designsystem.SumDiaryTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var container: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SumDiaryTheme {
                SumDiaryScreen(container)
            }
        }
    }
}
