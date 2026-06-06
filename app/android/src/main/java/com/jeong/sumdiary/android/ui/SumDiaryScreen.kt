package com.jeong.sumdiary.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jeong.sumdiary.android.R
import com.jeong.sumdiary.android.di.AppContainer
import com.jeong.sumdiary.core.designsystem.SumDiarySpacing
import com.jeong.sumdiary.domain.backup.BackupPassphrase
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.feature.backup.BackupIntent
import com.jeong.sumdiary.feature.backup.BackupState
import com.jeong.sumdiary.feature.backup.BackupUiStatus
import com.jeong.sumdiary.feature.entry.EntryIntent
import com.jeong.sumdiary.feature.entry.EntryState
import com.jeong.sumdiary.feature.summary.SummaryDisplayMessage
import com.jeong.sumdiary.feature.summary.SummaryIntent
import com.jeong.sumdiary.feature.summary.SummaryState
import com.jeong.sumdiary.feature.summary.SummaryUiStatus
import com.jeong.sumdiary.feature.summary.displayMessage
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private enum class SumDiaryTab(val title: String) {
    Diary("일기"),
    Summary("요약"),
    Settings("설정")
}

@OptIn(ExperimentalTime::class)
@Composable
fun SumDiaryScreen(
    container: AppContainer,
    appLockAvailable: Boolean,
    onRequestAppUnlock: (onResult: (success: Boolean, message: String?) -> Unit) -> Unit
) {
    val entryViewModel = remember { container.entryViewModel() }
    val summaryViewModel = remember { container.summaryViewModel() }
    val backupViewModel = remember { container.backupViewModel() }
    val entryState by entryViewModel.state.collectAsState()
    val summaryState by summaryViewModel.state.collectAsState()
    val backupState by backupViewModel.state.collectAsState()
    var firstRunGuideCompleted by remember {
        mutableStateOf(container.hasCompletedFirstRunGuide())
    }
    var appLockEnabled by remember { mutableStateOf(container.isAppLockEnabled()) }
    var appUnlocked by remember { mutableStateOf(!appLockEnabled) }
    var appLockMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(SumDiaryTab.Diary) }
    var showEntryEditor by remember { mutableStateOf(false) }
    var backupPassphraseAction by remember { mutableStateOf<BackupPassphraseAction?>(null) }
    var pendingDeleteEntryId by remember { mutableStateOf<String?>(null) }
    val today =
        remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }

    if (!firstRunGuideCompleted) {
        FirstRunGuideFlow(
            onComplete = {
                container.completeFirstRunGuide()
                firstRunGuideCompleted = true
                appUnlocked = !appLockEnabled
            }
        )
        return
    }

    if (appLockEnabled && !appUnlocked) {
        AppLockGateScreen(
            appLockAvailable = appLockAvailable,
            message = appLockMessage,
            onUnlock = {
                if (!appLockAvailable) {
                    appLockMessage = "이 기기에는 사용할 수 있는 생체 또는 화면 잠금 인증이 없어요."
                    return@AppLockGateScreen
                }
                onRequestAppUnlock { success, message ->
                    if (success) {
                        appUnlocked = true
                        appLockMessage = null
                    } else {
                        appLockMessage = message ?: "인증하지 못했어요. 다시 시도해 주세요."
                    }
                }
            }
        )
        return
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == SumDiaryTab.Summary) {
            summaryViewModel.dispatch(SummaryIntent.LoadDaily(today))
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SumDiaryTopTabs(
                selectedTab = selectedTab,
                onSelectTab = { selectedTab = it }
            )
        },
        floatingActionButton = {
            if (selectedTab == SumDiaryTab.Diary) {
                FloatingActionButton(
                    onClick = {
                        entryViewModel.dispatch(EntryIntent.CancelEdit)
                        showEntryEditor = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            SumDiaryTab.Diary -> DiaryTabContent(
                paddingValues = padding,
                state = entryState,
                onCreateEntry = {
                    entryViewModel.dispatch(EntryIntent.CancelEdit)
                    showEntryEditor = true
                },
                onEditEntry = { entry ->
                    entryViewModel.dispatch(EntryIntent.StartEdit(entry))
                    showEntryEditor = true
                }
            )
            SumDiaryTab.Summary -> SummaryTabContent(
                paddingValues = padding,
                state = summaryState,
                onIntent = { summaryViewModel.dispatch(it) }
            )
            SumDiaryTab.Settings -> SettingsTabContent(
                paddingValues = padding,
                backupState = backupState,
                onBackupIntent = { intent ->
                    when (intent) {
                        BackupIntent.ConnectGoogleDrive,
                        BackupIntent.DeleteRemoteBackup -> backupViewModel.dispatch(intent)
                        is BackupIntent.RunManualBackup,
                        is BackupIntent.RestoreMerge -> backupViewModel.dispatch(intent)
                    }
                },
                onRequestBackupPassphrase = { backupPassphraseAction = it },
                onShowGuide = {
                    container.resetFirstRunGuide()
                    firstRunGuideCompleted = false
                    appUnlocked = !appLockEnabled
                },
                appLockEnabled = appLockEnabled,
                appLockAvailable = appLockAvailable,
                onToggleAppLock = {
                    if (appLockEnabled) {
                        container.setAppLockEnabled(false)
                        appLockEnabled = false
                        appUnlocked = true
                        appLockMessage = "앱 잠금을 껐어요."
                        return@SettingsTabContent
                    }
                    if (!appLockAvailable) {
                        appLockMessage = "이 기기에는 사용할 수 있는 생체 또는 화면 잠금 인증이 없어요."
                        return@SettingsTabContent
                    }
                    onRequestAppUnlock { success, message ->
                        if (success) {
                            container.setAppLockEnabled(true)
                            appLockEnabled = true
                            appUnlocked = true
                            appLockMessage = "앱 잠금을 켰어요."
                        } else {
                            appLockMessage = message ?: "인증하지 못했어요. 다시 시도해 주세요."
                        }
                    }
                }
            )
        }
    }

    if (showEntryEditor) {
        EntryEditorDialog(
            text = entryState.text,
            editing = entryState.editingEntryId != null,
            saving = entryState.saving,
            onTextChange = { entryViewModel.dispatch(EntryIntent.EditText(it)) },
            onDismiss = {
                entryViewModel.dispatch(EntryIntent.CancelEdit)
                showEntryEditor = false
            },
            onSave = {
                entryViewModel.dispatch(EntryIntent.Save)
                showEntryEditor = false
            },
            onDelete = {
                pendingDeleteEntryId = entryState.editingEntryId
            }
        )
    }

    if (pendingDeleteEntryId != null) {
        DeleteEntryDialog(
            onDismiss = { pendingDeleteEntryId = null },
            onConfirm = {
                pendingDeleteEntryId?.let { entryViewModel.dispatch(EntryIntent.Delete(it)) }
                pendingDeleteEntryId = null
                showEntryEditor = false
            }
        )
    }

    backupPassphraseAction?.let { action ->
        BackupPassphraseDialog(
            action = action,
            onDismiss = { backupPassphraseAction = null },
            onConfirm = { passphrase ->
                backupViewModel.dispatch(action.toIntent(passphrase))
                backupPassphraseAction = null
            }
        )
    }

    appLockMessage?.let { message ->
        AppLockMessageDialog(
            message = message,
            onDismiss = { appLockMessage = null }
        )
    }
}

private enum class BackupPassphraseAction {
    Backup,
    Restore;

    fun toIntent(passphrase: BackupPassphrase): BackupIntent =
        when (this) {
            Backup -> BackupIntent.RunManualBackup(passphrase)
            Restore -> BackupIntent.RestoreMerge(passphrase)
    }
}

@Composable
private fun AppLockGateScreen(
    appLockAvailable: Boolean,
    message: String?,
    onUnlock: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SumDiarySpacing.Lg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Section)) {
                SumDiaryIllustration(
                    resourceId = R.drawable.sumdiary_biometric_lock,
                    contentDescription = "생체 인증 잠금 일러스트",
                    modifier = Modifier.size(112.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)) {
                    Text(
                        text = "SumDiary 잠김",
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = "일기와 백업 설정을 보려면 기기에 등록된 생체 또는 화면 잠금 인증이 필요해요.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                message?.let {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            modifier = Modifier.padding(SumDiarySpacing.Lg),
                            text = it,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = appLockAvailable,
                    onClick = onUnlock
                ) {
                    Text(text = "잠금 해제")
                }
                if (!appLockAvailable) {
                    Text(
                        text = "기기 설정에서 생체 인증 또는 화면 잠금을 먼저 등록해 주세요.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun AppLockMessageDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "확인")
            }
        },
        title = {
            Text(text = "앱 잠금")
        },
        text = {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}

@Composable
private fun FirstRunGuideFlow(onComplete: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    var showRequiredNotice by remember { mutableStateOf(false) }

    if (showRequiredNotice) {
        RequiredNoticeScreen(onComplete = onComplete)
    } else {
        AppGuideScreen(
            page = page,
            onNext = {
                if (page < AppGuidePage.entries.lastIndex) {
                    page += 1
                } else {
                    showRequiredNotice = true
                }
            },
            onSkip = { showRequiredNotice = true }
        )
    }
}

private enum class AppGuidePage(
    val title: String,
    val description: String,
    val previewTitle: String,
    val previewBody: String
) {
    Entry(
        title = "짧게 남기는 하루",
        description = "생각이 길어지기 전에 빠르게 기록해요.",
        previewTitle = "오늘의 기록",
        previewBody = "오후 9:12 · 회의 후 남은 생각을 정리했다"
    ),
    Summary(
        title = "기기 안에서 만드는 요약",
        description = "기록을 다시 읽기 쉽게 짧게 정리해요.",
        previewTitle = "기기 안에서 만든 요약",
        previewBody = "오늘은 정리와 회복이 함께 있던 날이에요."
    ),
    Backup(
        title = "사용자가 켜는 백업",
        description = "Google Drive 백업은 사용자가 선택한 뒤에만 동작해요.",
        previewTitle = "Google Drive 백업",
        previewBody = "암호화된 백업 · 아직 연결되지 않음"
    ),
    Security(
        title = "조용한 보호 설정",
        description = "생체 인증과 햅틱은 설정에서 직접 조정해요.",
        previewTitle = "생체 인증",
        previewBody = "OS 기본 인증 사용 · 꺼짐"
    )
}

@Composable
private fun AppGuideScreen(
    page: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val guidePage = AppGuidePage.entries[page]

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SumDiarySpacing.Lg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onSkip) {
                    Text(text = "건너뛰기")
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Section)) {
                Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)) {
                    Text(
                        text = guidePage.title,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = guidePage.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                AppGuidePreviewCard(guidePage)
                PageIndicators(currentPage = page, pageCount = AppGuidePage.entries.size)
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNext
            ) {
                Text(text = if (page == AppGuidePage.entries.lastIndex) "고지 확인하기" else "다음")
            }
        }
    }
}

@Composable
private fun AppGuidePreviewCard(page: AppGuidePage) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(SumDiarySpacing.Xl),
            verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SumDiaryIllustration(
                        resourceId = R.drawable.sumdiary_brand_mark_blueberry_lens,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = page.previewTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                StatusDot(label = "Preview")
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(SumDiarySpacing.Lg),
                    verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)
                ) {
                    Text(
                        text = page.previewBody,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "SumDiary 기본 화면과 같은 디자인을 사용해요.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun PageIndicators(currentPage: Int, pageCount: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(SumDiarySpacing.Xs)) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(width = if (currentPage == index) 20.dp else 8.dp, height = 8.dp)
                    .background(
                        color = if (currentPage == index) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = MaterialTheme.shapes.extraSmall
                    )
            )
        }
    }
}

@Composable
private fun RequiredNoticeScreen(onComplete: () -> Unit) {
    var sensitiveDataNoticeChecked by remember { mutableStateOf(false) }
    var onDeviceAiNoticeChecked by remember { mutableStateOf(false) }
    var backupOptInNoticeChecked by remember { mutableStateOf(false) }
    val allNoticesChecked = sensitiveDataNoticeChecked &&
        onDeviceAiNoticeChecked &&
        backupOptInNoticeChecked

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SumDiarySpacing.Lg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Section)) {
                Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)) {
                    Text(
                        text = "시작 전 확인",
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = "기능 소개와 별도로 꼭 알아야 할 내용이에요.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Md)) {
                    NoticeItem(
                        title = "민감정보가 포함될 수 있어요",
                        description = "일기에는 건강, 감정, 관계 같은 민감한 내용이 들어갈 수 있어요.",
                        checked = sensitiveDataNoticeChecked,
                        onCheckedChange = { sensitiveDataNoticeChecked = it }
                    )
                    NoticeItem(
                        title = "기록과 요약은 기본적으로 기기 안에서 처리해요",
                        description = "지원 기기와 OS 조건에 따라 온디바이스 AI 사용 가능 여부가 달라질 수 있어요.",
                        checked = onDeviceAiNoticeChecked,
                        onCheckedChange = { onDeviceAiNoticeChecked = it }
                    )
                    NoticeItem(
                        title = "백업은 사용자가 켠 뒤에만 동작해요",
                        description = "Google Drive 연결과 암호화 백업은 설정에서 직접 선택해야 해요.",
                        checked = backupOptInNoticeChecked,
                        onCheckedChange = { backupOptInNoticeChecked = it }
                    )
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = allNoticesChecked,
                onClick = onComplete
            ) {
                Text(text = if (allNoticesChecked) "확인하고 계속" else "필수 고지를 확인해 주세요")
            }
        }
    }
}

@Composable
private fun NoticeItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(SumDiarySpacing.Lg),
            horizontalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
            Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Xs)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SumDiaryTopTabs(
    selectedTab: SumDiaryTab,
    onSelectTab: (SumDiaryTab) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = SumDiarySpacing.Lg,
                        top = SumDiarySpacing.Xl,
                        end = SumDiarySpacing.Lg,
                        bottom = SumDiarySpacing.Sm
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Xs)) {
                    Text(
                        text = "SumDiary",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "짧게 쓰고, 기기 안에서 정리해요",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                StatusDot(label = "On-device")
            }
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                SumDiaryTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { onSelectTab(tab) },
                        text = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusDot(label: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SumDiarySpacing.Sm, vertical = SumDiarySpacing.Xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
            )
            Spacer(modifier = Modifier.width(SumDiarySpacing.Xs))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun DiaryTabContent(
    paddingValues: PaddingValues,
    state: EntryState,
    onCreateEntry: () -> Unit,
    onEditEntry: (DiaryEntry) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Md),
        contentPadding = PaddingValues(SumDiarySpacing.Lg)
    ) {
        item {
            ScreenSectionHeader(
                title = "오늘의 기록",
                description = "${state.date} · ${state.entries.size}개"
            )
        }
        if (state.entries.isEmpty()) {
            item {
                EmptyDiaryCard(onCreateEntry = onCreateEntry)
            }
        } else {
            items(state.entries, key = { it.id }) { entry ->
                DiaryEntryItem(
                    entry = entry,
                    onClick = { onEditEntry(entry) }
                )
            }
        }
    }
}

@Composable
private fun EmptyDiaryCard(onCreateEntry: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(SumDiarySpacing.Xl),
            verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Md)
        ) {
            SumDiaryIllustration(
                resourceId = R.drawable.sumdiary_empty_diary,
                contentDescription = "빈 일기 일러스트",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(112.dp)
            )
            Text(
                text = "아직 오늘 기록이 없어요",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "짧게 남기고 나중에 요약해 볼 수 있어요.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = onCreateEntry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "일기 쓰기")
            }
        }
    }
}

@Composable
private fun DiaryEntryItem(
    entry: DiaryEntry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(SumDiarySpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)
        ) {
            Text(
                text = entry.time.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = entry.content,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
@OptIn(ExperimentalTime::class)
private fun SummaryTabContent(
    paddingValues: PaddingValues,
    state: SummaryState,
    onIntent: (SummaryIntent) -> Unit
) {
    val today =
        remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(SumDiarySpacing.Lg),
        verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Md)
    ) {
        ScreenSectionHeader(
            title = "요약",
            description = "${state.period.first} ~ ${state.period.second}"
        )
        SummaryPanel(state)
        Row(horizontalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)) {
            Button(onClick = { onIntent(SummaryIntent.LoadDaily(today)) }) {
                Text(text = "오늘 요약")
            }
            Button(onClick = { onIntent(SummaryIntent.LoadWeekly(today)) }) {
                Text(text = "이번 주")
            }
        }
    }
}

@Composable
private fun SummaryPanel(state: SummaryState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(SumDiarySpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "기기 안에서 만든 요약",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = state.status.displayMessage.label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            when (state.status) {
                SummaryUiStatus.NOT_GENERATED -> SummaryStatusText(
                    message = state.status.displayMessage
                )
                SummaryUiStatus.LOADING -> {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    SummaryStatusText(
                        message = state.status.displayMessage
                    )
                }
                SummaryUiStatus.CONTENT -> {
                    Text(
                        text = state.text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (state.emotions.isNotEmpty()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = state.emotions.joinToString(prefix = "감정 태그: "),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                SummaryUiStatus.NO_ENTRIES -> SummaryStatusText(
                    message = state.status.displayMessage
                )
                SummaryUiStatus.UNSUPPORTED -> SummaryStatusText(
                    message = state.status.displayMessage
                )
                SummaryUiStatus.FAILED -> SummaryStatusText(
                    message = state.status.displayMessage
                )
            }
        }
    }
}

@Composable
private fun SummaryStatusText(
    message: SummaryDisplayMessage
) {
    Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Xs)) {
        Text(
            text = message.title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = message.description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SettingsTabContent(
    paddingValues: PaddingValues,
    backupState: BackupState,
    onBackupIntent: (BackupIntent) -> Unit,
    onRequestBackupPassphrase: (BackupPassphraseAction) -> Unit,
    onShowGuide: () -> Unit,
    appLockEnabled: Boolean,
    appLockAvailable: Boolean,
    onToggleAppLock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(SumDiarySpacing.Lg),
        verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Md)
    ) {
        ScreenSectionHeader(
            title = "설정",
            description = "백업, 보안, 개인정보"
        )
        BackupSettingsPanel(
            state = backupState,
            onIntent = onBackupIntent,
            onRequestPassphrase = onRequestBackupPassphrase
        )
        SettingsRow(
            title = "생체/기기 인증 앱 잠금",
            description = appLockDescription(
                enabled = appLockEnabled,
                available = appLockAvailable
            ),
            onClick = onToggleAppLock
        )
        SettingsRow(
            title = "앱 가이드 다시 보기",
            description = "첫 실행 안내를 다시 확인해요",
            onClick = onShowGuide
        )
    }
}

private fun appLockDescription(
    enabled: Boolean,
    available: Boolean
): String =
    when {
        enabled -> "켜짐 · 앱을 열 때 OS 인증으로 일기 화면을 보호해요."
        available -> "꺼짐 · 탭해서 생체 또는 화면 잠금 인증을 켤 수 있어요."
        else -> "사용 불가 · 기기 설정에 생체 인증 또는 화면 잠금을 먼저 등록해 주세요."
    }

@Composable
private fun BackupSettingsPanel(
    state: BackupState,
    onIntent: (BackupIntent) -> Unit,
    onRequestPassphrase: (BackupPassphraseAction) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, state.status.borderColor())
    ) {
        Column(
            modifier = Modifier.padding(SumDiarySpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SumDiaryIllustration(
                        resourceId = R.drawable.sumdiary_backup_cloud,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Xs)) {
                        Text(
                            text = "${state.providerName} 백업",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = state.statusTitle,
                            color = state.status.contentColor(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Text(
                    text = state.status.label,
                    color = state.status.contentColor(),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Text(
                text = state.statusDescription,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "백업 파일은 앱에서 암호화한 뒤 저장하고, 백업 비밀번호는 화면에 표시하지 않아요.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = state.accountRemovalNotice,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            state.lastResult?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (state.busy) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = !state.busy,
                    onClick = { onIntent(BackupIntent.ConnectGoogleDrive) }
                ) {
                    Text(text = if (state.connected) "연결 확인" else "연결")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = !state.busy,
                    onClick = { onRequestPassphrase(BackupPassphraseAction.Backup) }
                ) {
                    Text(text = "백업")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    enabled = !state.busy,
                    onClick = { onRequestPassphrase(BackupPassphraseAction.Restore) }
                ) {
                    Text(text = "복구")
                }
                TextButton(
                    modifier = Modifier.weight(1f),
                    enabled = !state.busy,
                    onClick = { onIntent(BackupIntent.DeleteRemoteBackup) }
                ) {
                    Text(text = "백업 삭제")
                }
            }
        }
    }
}

@Composable
private fun BackupPassphraseDialog(
    action: BackupPassphraseAction,
    onDismiss: () -> Unit,
    onConfirm: (BackupPassphrase) -> Unit
) {
    var passphraseText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    val title = when (action) {
        BackupPassphraseAction.Backup -> "백업 비밀번호 입력"
        BackupPassphraseAction.Restore -> "복구 비밀번호 입력"
    }
    val description = when (action) {
        BackupPassphraseAction.Backup -> "이 비밀번호로 백업 파일을 암호화해요. 잃어버리면 새 기기에서 복구할 수 없어요."
        BackupPassphraseAction.Restore -> "백업을 만들 때 사용한 비밀번호를 입력해야 복구할 수 있어요."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)) {
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = passphraseText,
                    onValueChange = {
                        passphraseText = it
                        errorText = null
                    },
                    label = { Text("백업 비밀번호") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = errorText != null,
                    supportingText = {
                        Text(
                            text = errorText
                                ?: "${BackupPassphrase.MinimumLength}자 이상 입력해 주세요."
                        )
                    },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    runCatching { BackupPassphrase(passphraseText) }
                        .onSuccess(onConfirm)
                        .onFailure {
                            errorText = "${BackupPassphrase.MinimumLength}자 이상 입력해야 해요."
                        }
                }
            ) {
                Text(text = if (action == BackupPassphraseAction.Backup) "백업" else "복구")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        }
    )
}

private val BackupUiStatus.label: String
    get() = when (this) {
        BackupUiStatus.NotConnected -> "꺼짐"
        BackupUiStatus.Ready -> "준비"
        BackupUiStatus.Running -> "진행"
        BackupUiStatus.Success -> "완료"
        BackupUiStatus.NeedsConnection -> "연결 필요"
        BackupUiStatus.Failed -> "확인 필요"
    }

@Composable
private fun BackupUiStatus.contentColor() = when (this) {
    BackupUiStatus.Success,
    BackupUiStatus.Ready -> MaterialTheme.colorScheme.primary
    BackupUiStatus.Failed,
    BackupUiStatus.NeedsConnection -> MaterialTheme.colorScheme.error
    BackupUiStatus.NotConnected,
    BackupUiStatus.Running -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun BackupUiStatus.borderColor() = when (this) {
    BackupUiStatus.Failed,
    BackupUiStatus.NeedsConnection -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.outline
}

@Composable
private fun SettingsRow(
    title: String,
    description: String,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(SumDiarySpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Xs)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ScreenSectionHeader(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Xs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EntryEditorDialog(
    text: String,
    editing: Boolean,
    saving: Boolean,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onSave,
                enabled = text.isNotBlank() && !saving
            ) {
                Text(text = if (saving) "저장 중" else "저장")
            }
        },
        dismissButton = {
            Row {
                if (editing) {
                    TextButton(onClick = onDelete) {
                        Text(
                            text = "삭제",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(text = "취소")
                }
            }
        },
        title = {
            Text(text = if (editing) "일기 수정" else "새 일기")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)
            ) {
                Text(
                    text = if (editing) {
                        "수정 중 뒤로 가면 변경사항은 저장되지 않아요."
                    } else {
                        "오늘 기억할 문장만 짧게 남겨도 좋아요."
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 144.dp),
                    placeholder = { Text(text = "오늘 있었던 일") },
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
        }
    )
}

@Composable
private fun DeleteEntryDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "삭제",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        },
        title = {
            Text(text = "일기를 삭제할까요?")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Md)) {
                SumDiaryIllustration(
                    resourceId = R.drawable.sumdiary_delete_data,
                    contentDescription = "삭제 경고 일러스트",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(96.dp)
                )
                Text(
                    text = "이 작업은 로컬에 저장된 일기를 삭제해요. 백업이 켜져 있다면 다음 백업에서 삭제 상태가 반영될 수 있어요.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

@Composable
private fun SumDiaryIllustration(
    resourceId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = resourceId),
        contentDescription = contentDescription,
        modifier = modifier
    )
}
