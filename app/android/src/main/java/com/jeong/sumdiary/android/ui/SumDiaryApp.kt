package com.jeong.sumdiary.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Add
import androidx.compose.material3.icons.filled.Summarize
import androidx.compose.material3.icons.filled.ViewList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jeong.sumdiary.android.di.AndroidServiceLocator
import com.jeong.sumdiary.core.designsystem.theme.SumDiaryTheme
import com.jeong.sumdiary.domain.diary.model.DiaryEntry
import com.jeong.sumdiary.feature.entry.EntryIntent
import com.jeong.sumdiary.feature.entry.EntryState
import com.jeong.sumdiary.feature.summary.SummaryIntent
import com.jeong.sumdiary.feature.summary.SummaryState
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.dayOfWeek
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.minus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SumDiaryApp() {
    val entryViewModel = remember { AndroidServiceLocator.provideEntryViewModel() }
    val summaryViewModel = remember { AndroidServiceLocator.provideSummaryViewModel() }
    val diaryRepository = remember { AndroidServiceLocator.diaryRepository() }

    DisposableEffect(Unit) {
        onDispose {
            entryViewModel.dispose()
            summaryViewModel.dispose()
        }
    }

    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val range = remember(today) { (today.minus(DatePeriod(days = 6))) to today }
    val diaries by remember(range) {
        diaryRepository.observeRange(range.first, range.second)
    }.collectAsState(initial = emptyList())

    val entryState by entryViewModel.state.collectAsState()
    val summaryState by summaryViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        summaryViewModel.handle(SummaryIntent.LoadDaily(today))
    }

    var selectedTab by remember { mutableStateOf(HomeTab.Diary) }
    var showEditor by remember { mutableStateOf(false) }

    SumDiaryTheme(darkTheme = false) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "SumDiary") })
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == HomeTab.Diary,
                        onClick = { selectedTab = HomeTab.Diary },
                        icon = { Icon(Icons.Default.ViewList, contentDescription = null) },
                        label = { Text("일기") },
                    )
                    NavigationBarItem(
                        selected = selectedTab == HomeTab.Summary,
                        onClick = { selectedTab = HomeTab.Summary },
                        icon = { Icon(Icons.Default.Summarize, contentDescription = null) },
                        label = { Text("요약") },
                    )
                }
            },
            floatingActionButton = {
                if (selectedTab == HomeTab.Diary) {
                    FloatingActionButton(onClick = { showEditor = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            },
        ) { paddingValues ->
            when (selectedTab) {
                HomeTab.Diary -> DiaryListScreen(
                    modifier = Modifier.padding(paddingValues),
                    entries = diaries,
                )

                HomeTab.Summary -> SummaryScreen(
                    modifier = Modifier.padding(paddingValues),
                    state = summaryState,
                    onLoadDaily = { summaryViewModel.handle(SummaryIntent.LoadDaily(today)) },
                    onLoadWeekly = {
                        val weekStart = today.minus(DatePeriod(days = today.dayOfWeek.ordinal))
                        summaryViewModel.handle(SummaryIntent.LoadWeekly(weekStart))
                    },
                )
            }
        }

        if (showEditor) {
            EntryEditor(
                state = entryState,
                onTextChange = { entryViewModel.handle(EntryIntent.Edit(it)) },
                onDismiss = { showEditor = false },
                onSave = {
                    entryViewModel.handle(EntryIntent.Save)
                    showEditor = false
                },
            )
        }
    }
}

enum class HomeTab { Diary, Summary }

@Composable
private fun DiaryListScreen(
    modifier: Modifier = Modifier,
    entries: List<DiaryEntry>,
) {
    if (entries.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "작성된 일기가 없습니다.", style = MaterialTheme.typography.titleMedium)
            Text(text = "오른쪽 아래 버튼을 눌러 일기를 추가해보세요.", textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(entries) { entry ->
                DiaryCard(entry = entry)
            }
        }
    }
}

@Composable
private fun DiaryCard(entry: DiaryEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        Text(
            text = "${'$'}{entry.date} ${'$'}{entry.time}",
            style = MaterialTheme.typography.labelMedium
        )
        Text(text = entry.content, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun EntryEditor(
    state: EntryState,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "일기 작성") },
        text = {
            Column {
                TextField(
                    value = state.text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "오늘의 이야기를 적어주세요") },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !state.saving && state.text.isNotBlank()) {
                Text(text = "저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "닫기") }
        },
    )
}

@Composable
private fun SummaryScreen(
    modifier: Modifier = Modifier,
    state: SummaryState,
    onLoadDaily: () -> Unit,
    onLoadWeekly: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "${'$'}{state.period.first} ~ ${'$'}{state.period.second}",
            style = MaterialTheme.typography.titleMedium
        )
        Text(text = state.text.ifBlank { "요약이 준비되지 않았습니다." })
        Text(text = "감정 태그: ${'$'}{state.emotions.joinToString().ifBlank { " 없음 " }}")
        RowActionButtons(onLoadDaily = onLoadDaily, onLoadWeekly = onLoadWeekly)
    }
}

@Composable
private fun RowActionButtons(
    onLoadDaily: () -> Unit,
    onLoadWeekly: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(onClick = onLoadDaily) { Text("오늘 요약") }
        Button(onClick = onLoadWeekly) { Text("이번 주 요약") }
    }
}
