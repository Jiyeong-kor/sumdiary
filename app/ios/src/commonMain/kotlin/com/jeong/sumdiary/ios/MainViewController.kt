package com.jeong.sumdiary.ios

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.jeong.sumdiary.core.util.DateUtils
import com.jeong.sumdiary.core.util.Logger
import com.jeong.sumdiary.data.diary.createDiaryRepository
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.summary.PlaceholderSummarizerEngine
import com.jeong.sumdiary.data.summary.createSummaryRepository
import com.jeong.sumdiary.data.summary.db.SummaryDatabase
import com.jeong.sumdiary.domain.summary.GenerateDailySummary
import com.jeong.sumdiary.domain.summary.GenerateWeeklySummary
import com.jeong.sumdiary.feature.entry.EntryViewModel
import com.jeong.sumdiary.feature.summary.SummaryIntent
import com.jeong.sumdiary.feature.summary.SummaryViewModel
import kotlinx.coroutines.runBlocking
import platform.UIKit.NSLayoutConstraintActivate
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.UIKit.UIViewController

class IOSAppContainer {
    private val diaryDriver = NativeSqliteDriver(DiaryDatabase.Schema, "diary.db")
    private val summaryDriver = NativeSqliteDriver(SummaryDatabase.Schema, "summary.db")

    private val diaryRepository = createDiaryRepository(diaryDriver)
    private val summaryRepository = createSummaryRepository(summaryDriver, PlaceholderSummarizerEngine())

    private val dailySummary = GenerateDailySummary(diaryRepository, summaryRepository)
    private val weeklySummary = GenerateWeeklySummary(diaryRepository, summaryRepository)

    val entryViewModel: EntryViewModel = EntryViewModel(diaryRepository)
    val summaryViewModel: SummaryViewModel = SummaryViewModel(dailySummary, weeklySummary)
}

fun MainViewController(): UIViewController {
    Logger.initialize()
    val container = IOSAppContainer()
    val controller = UIViewController()
    controller.view.backgroundColor = UIColor.systemBackgroundColor

    val label = UILabel().apply {
        text = "SumDiary iOS 준비 중"
        textColor = UIColor.labelColor
        font = UIFont.systemFontOfSize(20.0)
        translatesAutoresizingMaskIntoConstraints = false
    }
    controller.view.addSubview(label)
    NSLayoutConstraintActivate(
        listOf(
            label.centerXAnchor.constraintEqualToAnchor(controller.view.centerXAnchor),
            label.centerYAnchor.constraintEqualToAnchor(controller.view.centerYAnchor),
        )
    )

    runBlocking {
        container.summaryViewModel.dispatch(SummaryIntent.LoadDaily(DateUtils.nowDate()))
    }

    return controller
}
