package com.jeong.sumdiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jeong.sumdiary.android.ui.SumDiaryScreen
import com.jeong.sumdiary.core.designsystem.SumDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as SumDiaryApplication).container
        setContent {
            SumDiaryTheme {
                SumDiaryScreen(container)
            }
        }
    }
}
