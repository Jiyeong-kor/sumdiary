package com.jeong.sumdiary.android

import android.app.Application
import com.jeong.sumdiary.core.util.NapierLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SumDiaryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NapierLogger.init(enableDebug = true)
    }
}
