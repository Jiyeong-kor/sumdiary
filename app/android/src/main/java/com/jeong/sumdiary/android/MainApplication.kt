package com.jeong.sumdiary.android

import android.app.Application
import com.jeong.sumdiary.core.util.logging.Logger

class MainApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        Logger.init()
        container = AppContainer(this)
    }
}
