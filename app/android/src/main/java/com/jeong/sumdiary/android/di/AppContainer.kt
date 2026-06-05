package com.jeong.sumdiary.android.di

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jeong.sumdiary.android.backup.DelegatingGoogleDriveAccessTokenProvider
import com.jeong.sumdiary.core.util.DefaultDispatchersProvider
import com.jeong.sumdiary.data.backup.AndroidBackupCipher
import com.jeong.sumdiary.data.backup.BackupRepositoryImpl
import com.jeong.sumdiary.data.backup.DatabaseBackupSnapshotRepository
import com.jeong.sumdiary.data.backup.GoogleDriveBackupCloudRepository
import com.jeong.sumdiary.data.backup.GoogleDriveBackupConfiguration
import com.jeong.sumdiary.data.backup.GoogleDriveAccessTokenProvider
import com.jeong.sumdiary.data.backup.RealBackupEncryptor
import com.jeong.sumdiary.android.R
import com.jeong.sumdiary.data.diary.DiaryRepositoryImpl
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.summary.AndroidMlKitSummarizerEngine
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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppContainer @Inject constructor(
    @ApplicationContext context: Context
) {

    private val preferences = context.getSharedPreferences("sumdiary_app", Context.MODE_PRIVATE)
    private val dispatchers = DefaultDispatchersProvider

    private val diaryDatabase = DiaryDatabase(
        AndroidSqliteDriver(DiaryDatabase.Schema, context, "diary.db")
    )

    private val summaryDatabase = SummaryDatabase(
        AndroidSqliteDriver(SummaryDatabase.Schema, context, "summary.db")
    )

    private val diaryRepository = DiaryRepositoryImpl(diaryDatabase, dispatchers.io)
    private val summarizerEngine = AndroidMlKitSummarizerEngine(context)
    private val summaryRepository = SummaryRepositoryImpl(
        summaryDatabase,
        dispatchers.io,
        summarizerEngine
    )

    private val generateDailySummary = GenerateDailySummary(diaryRepository, summaryRepository)
    private val generateWeeklySummary = GenerateWeeklySummary(diaryRepository, summaryRepository)
    private val googleDriveAccessTokenProvider = DelegatingGoogleDriveAccessTokenProvider()
    private val backupCloudRepository = GoogleDriveBackupCloudRepository(
        configuration = GoogleDriveBackupConfiguration(
            oauthClientId = context.getString(R.string.google_drive_oauth_client_id)
        ),
        accessTokenProvider = googleDriveAccessTokenProvider
    )
    private val backupRepository = BackupRepositoryImpl(
        snapshotRepository = DatabaseBackupSnapshotRepository(
            diaryDatabase = diaryDatabase,
            summaryDatabase = summaryDatabase,
            dispatcher = dispatchers.io
        ),
        backupEncryptor = RealBackupEncryptor(AndroidBackupCipher()),
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
        cloudRepository = backupCloudRepository,
        runBackup = runBackup,
        restoreBackup = restoreBackup,
        deleteRemoteBackup = deleteRemoteBackup,
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

    fun isAppLockEnabled(): Boolean =
        preferences.getBoolean(KEY_APP_LOCK_ENABLED, false)

    fun setAppLockEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_APP_LOCK_ENABLED, enabled).apply()
    }

    fun setGoogleDriveAccessTokenProvider(provider: GoogleDriveAccessTokenProvider) {
        googleDriveAccessTokenProvider.setDelegate(provider)
    }

    private companion object {
        const val KEY_FIRST_RUN_GUIDE_COMPLETED = "first_run_guide_completed"
        const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
    }
}
