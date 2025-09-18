package com.jeong.sumdiary.data.diary

import app.cash.sqldelight.db.SqlDriver
import com.jeong.sumdiary.data.diary.db.DiaryDatabase

class DiaryDatabaseFactory(private val driver: SqlDriver) {
    fun create(): DiaryDatabase = DiaryDatabase(driver)
}
