package com.jeong.sumdiary.data.summary

import app.cash.sqldelight.db.SqlDriver
import com.jeong.sumdiary.data.summary.db.SummaryDatabase

class SummaryDatabaseFactory(private val driver: SqlDriver) {
    fun create(): SummaryDatabase = SummaryDatabase(driver)
}
