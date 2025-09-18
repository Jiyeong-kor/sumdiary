package com.jeong.sumdiary.android

import android.app.Application
import com.jeong.sumdiary.android.di.AppContainer
import com.jeong.sumdiary.core.util.NapierLogger

class SumDiaryApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        NapierLogger.init(enableDebug = true)
        container = AppContainer(this)
    }
}
