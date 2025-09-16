package com.jeong.sumdiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jeong.sumdiary.core.designsystem.SumDiaryTheme
import com.jeong.sumdiary.core.util.DateUtils
import com.jeong.sumdiary.feature.entry.EntryIntent
import com.jeong.sumdiary.feature.entry.EntryViewModel
import com.jeong.sumdiary.feature.summary.SummaryIntent
import com.jeong.sumdiary.feature.summary.SummaryViewModel

class MainActivity : ComponentActivity() {
    private val container by lazy { (application as SumDiaryApp).container }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SumDiaryTheme {
                MainScreen(
                    entryViewModel = container.entryViewModel,
                    summaryViewModel = container.summaryViewModel,
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        container.entryViewModel.clear()
        container.summaryViewModel.clear()
    }
}

private enum class MainTab { DIARY, SUMMARY }

@Composable
private fun MainScreen(
    entryViewModel: EntryViewModel,
    summaryViewModel: SummaryViewModel,
) {
    var selectedTab by remember { mutableIntStateOf(MainTab.DIARY.ordinal) }
    var showEntry by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (!showEntry && MainTab.values()[selectedTab] == MainTab.DIARY) {
                FloatingActionButton(onClick = { showEntry = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "새 기록")
                }
            }
        }
    ) { padding ->
        if (showEntry) {
            EntryScreen(
                modifier = Modifier.padding(padding),
                viewModel = entryViewModel,
                onClose = { showEntry = false },
            )
        } else {
            Column(modifier = Modifier.padding(padding)) {
                TabRow(selectedTabIndex = selectedTab) {
                    MainTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab.ordinal,
                            onClick = { selectedTab = tab.ordinal },
                            text = { Text(if (tab == MainTab.DIARY) "기록" else "요약") },
                        )
                    }
                }
                when (MainTab.values()[selectedTab]) {
                    MainTab.DIARY -> DiaryList()
                    MainTab.SUMMARY -> SummaryScreen(summaryViewModel = summaryViewModel)
                }
            }
        }
    }
}

@Composable
private fun DiaryList() {
    val dummy = remember {
        List(5) { index -> "오늘의 다이어리 #$index" }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(dummy) { item ->
            Text(text = item)
        }
    }
}

@Composable
private fun EntryScreen(
    modifier: Modifier,
    viewModel: EntryViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "새로운 일기")
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.text,
            onValueChange = { viewModel.dispatch(EntryIntent.Edit(it)) },
            placeholder = { Text("오늘은 어떤 하루였나요?") },
            enabled = !state.saving,
        )
        Button(
            onClick = { viewModel.dispatch(EntryIntent.Save); onClose() },
            enabled = state.text.isNotBlank() && !state.saving,
        ) {
            Text("저장")
        }
        Button(onClick = onClose) {
            Text("닫기")
        }
    }
}

@Composable
private fun SummaryScreen(summaryViewModel: SummaryViewModel) {
    val state by summaryViewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        summaryViewModel.dispatch(SummaryIntent.LoadDaily(DateUtils.nowDate()))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = "요약 기간: ${state.period.first} ~ ${state.period.second}")
        if (state.loading) {
            CircularProgressIndicator()
        } else {
            Text(text = state.text)
            if (state.emotions.isNotEmpty()) {
                Text(text = "감정 태그: ${state.emotions.joinToString()}")
            }
            Button(onClick = {
                summaryViewModel.dispatch(SummaryIntent.LoadWeekly(state.period.first))
            }) {
                Text("주간 요약 다시 생성")
            }
        }
    }
}
