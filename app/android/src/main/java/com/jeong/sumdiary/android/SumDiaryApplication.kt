package com.jeong.sumdiary.android

import android.app.Application
import com.jeong.sumdiary.android.di.AndroidServiceLocator
import com.jeong.sumdiary.core.util.Logger

class SumDiaryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.init(BuildConfig.DEBUG)
        AndroidServiceLocator.initialize(this)
    }
}
