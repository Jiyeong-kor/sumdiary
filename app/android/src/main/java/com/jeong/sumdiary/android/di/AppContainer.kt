package com.jeong.sumdiary.android.di

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jeong.sumdiary.core.util.DefaultDispatchersProvider
import com.jeong.sumdiary.data.backup.BackupRepositoryImpl
import com.jeong.sumdiary.data.backup.DevelopmentBackupSnapshotRepository
import com.jeong.sumdiary.data.backup.FakeBackupEncryptor
import com.jeong.sumdiary.data.backup.InMemoryBackupCloudRepository
import com.jeong.sumdiary.data.diary.DiaryRepositoryImpl
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.summary.PlatformSummarizerProvider
import com.jeong.sumdiary.data.summary.SummaryRepositoryImpl
import com.jeong.sumdiary.data.summary.db.SummaryDatabase
import com.jeong.sumdiary.domain.backup.BackupProvider
import com.jeong.sumdiary.domain.backup.usecase.DeleteRemoteBackup
import com.jeong.sumdiary.domain.backup.usecase.RestoreBackup
import com.jeong.sumdiary.domain.backup.usecase.RunBackup
import com.jeong.sumdiary.feature.backup.BackupViewModel
import com.jeong.sumdiary.domain.summary.usecase.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.usecase.GenerateWeeklySummary
import com.jeong.sumdiary.feature.entry.EntryViewModel
import com.jeong.sumdiary.feature.summary.SummaryViewModel

class AppContainer(context: Context) {

    private val preferences = context.getSharedPreferences("sumdiary_app", Context.MODE_PRIVATE)
    private val dispatchers = DefaultDispatchersProvider

    private val diaryDatabase = DiaryDatabase(
        AndroidSqliteDriver(DiaryDatabase.Schema, context, "diary.db")
    )

    private val summaryDatabase = SummaryDatabase(
        AndroidSqliteDriver(SummaryDatabase.Schema, context, "summary.db")
    )

    private val diaryRepository = DiaryRepositoryImpl(diaryDatabase, dispatchers.io)
    private val summarizerEngine = PlatformSummarizerProvider.create()
    private val summaryRepository = SummaryRepositoryImpl(
        summaryDatabase,
        dispatchers.io,
        summarizerEngine
    )

    private val generateDailySummary = GenerateDailySummary(diaryRepository, summaryRepository)
    private val generateWeeklySummary = GenerateWeeklySummary(diaryRepository, summaryRepository)
    private val backupCloudRepository = InMemoryBackupCloudRepository()
    private val backupRepository = BackupRepositoryImpl(
        snapshotRepository = DevelopmentBackupSnapshotRepository(),
        backupEncryptor = FakeBackupEncryptor(),
        cloudRepository = backupCloudRepository
    )
    private val runBackup = RunBackup(backupRepository)
    private val restoreBackup = RestoreBackup(backupRepository)
    private val deleteRemoteBackup = DeleteRemoteBackup(backupRepository)

    fun entryViewModel(): EntryViewModel = EntryViewModel(diaryRepository, dispatchers.io)

    fun summaryViewModel(): SummaryViewModel = SummaryViewModel(
        generateDailySummary,
        generateWeeklySummary,
        dispatchers.io
    )

    fun backupViewModel(): BackupViewModel = BackupViewModel(
        provider = BackupProvider.GoogleDrive,
        runBackup = runBackup,
        restoreBackup = restoreBackup,
        deleteRemoteBackup = deleteRemoteBackup,
        connectProviderForDevelopment = { backupCloudRepository.connectForDevelopment() },
        dispatcher = dispatchers.io
    )

    fun hasCompletedFirstRunGuide(): Boolean =
        preferences.getBoolean(KEY_FIRST_RUN_GUIDE_COMPLETED, false)

    fun completeFirstRunGuide() {
        preferences.edit().putBoolean(KEY_FIRST_RUN_GUIDE_COMPLETED, true).apply()
    }

    fun resetFirstRunGuide() {
        preferences.edit().putBoolean(KEY_FIRST_RUN_GUIDE_COMPLETED, false).apply()
    }

    private companion object {
        const val KEY_FIRST_RUN_GUIDE_COMPLETED = "first_run_guide_completed"
    }
}
