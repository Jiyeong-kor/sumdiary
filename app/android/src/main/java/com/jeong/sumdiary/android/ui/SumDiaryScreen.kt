package com.jeong.sumdiary.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.jeong.sumdiary.android.di.AppContainer
import com.jeong.sumdiary.feature.entry.EntryIntent
import com.jeong.sumdiary.feature.summary.SummaryIntent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun SumDiaryScreen(container: AppContainer) {
    val entryViewModel = remember { container.entryViewModel() }
    val summaryViewModel = remember { container.summaryViewModel() }
    val entryState by entryViewModel.state.collectAsState()
    val summaryState by summaryViewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showEntryEditor by remember { mutableStateOf(false) }
    val today =
        remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            summaryViewModel.dispatch(SummaryIntent.LoadDaily(today))
        }
    }

    Scaffold(
        topBar = {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text(text = "일기")
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text(text = "요약")
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { showEntryEditor = true }) {
                    Text(text = "+")
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> DiaryTabContent(padding)
            else -> SummaryTabContent(padding, summaryState) {
                summaryViewModel.dispatch(it)
            }
        }
    }

    if (showEntryEditor) {
        EntryEditorDialog(
            text = entryState.text,
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
private fun DiaryTabContent(paddingValues: PaddingValues) {
    val dummyEntries = remember {
        listOf("오늘의 샘플 일기", "AI 요약을 확인해보세요")
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(dummyEntries) { text ->
            Text(text = text)
        }
    }
}

@Composable
private fun SummaryTabContent(
    paddingValues: PaddingValues,
    state: com.jeong.sumdiary.feature.summary.SummaryState,
    onIntent: (SummaryIntent) -> Unit
) {
    val today =
        remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "기간: ${state.period.first} ~ ${state.period.second}")
        Text(text = state.text)
        Text(text = "감정 태그: ${state.emotions.joinToString()}")
        Button(onClick = { onIntent(SummaryIntent.LoadDaily(today)) }) {
            Text(text = "오늘 요약")
        }
        Button(onClick = { onIntent(SummaryIntent.LoadWeekly(today)) }) {
            Text(text = "이번 주 요약")
        }
    }
}

@Composable
private fun EntryEditorDialog(
    text: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(text = "저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "새 일기")
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    )
}
