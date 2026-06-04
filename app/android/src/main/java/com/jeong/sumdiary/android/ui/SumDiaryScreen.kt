package com.jeong.sumdiary.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jeong.sumdiary.android.di.AppContainer
import com.jeong.sumdiary.core.designsystem.SumDiarySpacing
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.feature.entry.EntryIntent
import com.jeong.sumdiary.feature.entry.EntryState
import com.jeong.sumdiary.feature.summary.SummaryIntent
import com.jeong.sumdiary.feature.summary.SummaryState
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
fun SumDiaryScreen(container: AppContainer) {
    val entryViewModel = remember { container.entryViewModel() }
    val summaryViewModel = remember { container.summaryViewModel() }
    val entryState by entryViewModel.state.collectAsState()
    val summaryState by summaryViewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(SumDiaryTab.Diary) }
    var showEntryEditor by remember { mutableStateOf(false) }
    val today =
        remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }

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
                    onClick = { showEntryEditor = true },
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
                onCreateEntry = { showEntryEditor = true }
            )
            SumDiaryTab.Summary -> SummaryTabContent(
                paddingValues = padding,
                state = summaryState,
                onIntent = { summaryViewModel.dispatch(it) }
            )
            SumDiaryTab.Settings -> SettingsTabContent(padding)
        }
    }

    if (showEntryEditor) {
        EntryEditorDialog(
            text = entryState.text,
            saving = entryState.saving,
            onTextChange = { entryViewModel.dispatch(EntryIntent.EditText(it)) },
            onDismiss = { showEntryEditor = false },
            onSave = {
                entryViewModel.dispatch(EntryIntent.Save)
                showEntryEditor = false
            }
        )
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
    onCreateEntry: () -> Unit
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
                DiaryEntryItem(entry)
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
private fun DiaryEntryItem(entry: DiaryEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    text = if (state.loading) "생성 중" else "준비됨",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            if (state.loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "일기 원문은 이 기기 안에서 처리해요.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
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
        }
    }
}

@Composable
private fun SettingsTabContent(paddingValues: PaddingValues) {
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
        SettingsRow(title = "Google Drive 백업", description = "사용자가 켠 뒤에만 동작해요")
        SettingsRow(title = "생체 인증", description = "OS 기본 인증을 사용해요")
        SettingsRow(title = "앱 가이드 다시 보기", description = "첫 실행 안내를 다시 확인해요")
    }
}

@Composable
private fun SettingsRow(title: String, description: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
    saving: Boolean,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
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
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        },
        title = {
            Text(text = "새 일기")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SumDiarySpacing.Sm)
            ) {
                Text(
                    text = "오늘 기억할 문장만 짧게 남겨도 좋아요.",
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
