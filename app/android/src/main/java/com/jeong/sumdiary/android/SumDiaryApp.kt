package com.jeong.sumdiary.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jeong.sumdiary.core.designsystem.theme.SumDiaryTheme
import com.jeong.sumdiary.core.util.time.TimeProvider
import com.jeong.sumdiary.feature.entry.EntryIntent
import com.jeong.sumdiary.feature.entry.EntryViewModel
import com.jeong.sumdiary.feature.summary.SummaryIntent
import com.jeong.sumdiary.feature.summary.SummaryViewModel
import kotlinx.datetime.TimeZone

private enum class HomeTab(val title: String) {
    DIARY("일기"),
    SUMMARY("요약")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SumDiaryApp(container: AppContainer) {
    var currentTab by remember { mutableStateOf(HomeTab.DIARY) }
    val entryViewModel = remember { EntryViewModel(container.diaryRepository) }
    val summaryViewModel = remember {
        SummaryViewModel(
            generateDailySummary = container.generateDailySummary,
            generateWeeklySummary = container.generateWeeklySummary
        )
    }
    val entryState by entryViewModel.state.collectAsState()
    val summaryState by summaryViewModel.state.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            entryViewModel.clear()
            summaryViewModel.clear()
        }
    }
    LaunchedEffect(Unit) {
        val today = TimeProvider.today(TimeZone.currentSystemDefault())
        summaryViewModel.onIntent(SummaryIntent.LoadDaily(today))
    }
    SumDiaryTheme(darkTheme = false) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        HomeTab.values().forEach { tab ->
                            NavigationBarItem(
                                selected = tab == currentTab,
                                onClick = { currentTab = tab },
                                label = { Text(tab.title) },
                                icon = {}
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (currentTab == HomeTab.DIARY) {
                        FloatingActionButton(onClick = { /* TODO: 화면 전환 */ }) {
                            Text(text = "+")
                        }
                    }
                },
            ) { padding ->
                when (currentTab) {
                    HomeTab.DIARY -> DiaryList(
                        modifier = Modifier.padding(padding),
                        onEdit = { text ->
                            val now = TimeProvider.now(TimeZone.currentSystemDefault())
                            entryViewModel.onIntent(
                                EntryIntent.Edit(
                                    text = text,
                                    date = now.date,
                                    time = now.time,
                                ),
                            )
                        },
                        onSave = { entryViewModel.onIntent(EntryIntent.Save) },
                        stateText = entryState.text,
                        isSaving = entryState.saving,
                    )

                    HomeTab.SUMMARY -> SummaryScreen(
                        modifier = Modifier.padding(padding),
                        text = summaryState.text,
                        emotions = summaryState.emotions,
                        loading = summaryState.loading,
                        onReload = {
                            summaryViewModel.onIntent(
                                SummaryIntent.LoadDaily(
                                    TimeProvider.today(TimeZone.currentSystemDefault())
                                ),
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryList(
    modifier: Modifier = Modifier,
    onEdit: (String) -> Unit,
    onSave: () -> Unit,
    stateText: String,
    isSaving: Boolean,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "오늘의 일기", style = MaterialTheme.typography.headlineMedium)
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(DUMMY_DIARIES) { diary ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = diary.title, fontWeight = FontWeight.Bold)
                        Text(text = diary.content, modifier = Modifier.padding(top = 8.dp))
                        TextButton(onClick = { onEdit(diary.content) }) {
                            Text(text = "이 내용으로 작성")
                        }
                    }
                }
            }
        }
        TextField(
            value = stateText,
            onValueChange = { onEdit(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("새 일기") },
        )
        TextButton(onClick = onSave, enabled = !isSaving) {
            Text(if (isSaving) "저장 중" else "저장")
        }
    }
}

@Composable
private fun SummaryScreen(
    modifier: Modifier = Modifier,
    text: String,
    emotions: List<String>,
    loading: Boolean,
    onReload: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = "요약", style = MaterialTheme.typography.headlineMedium)
        if (loading) {
            Text(text = "요약을 불러오는 중입니다...")
        } else {
            Text(text = if (text.isBlank()) "요약이 아직 없습니다." else text)
            if (emotions.isNotEmpty()) {
                Text(text = "감정 태그: ${'$'}{emotions.joinToString(", ")}")
            }
        }
        TextButton(onClick = onReload) {
            Text(text = "다시 불러오기")
        }
    }
}

private data class DummyDiary(val title: String, val content: String)

private val DUMMY_DIARIES = listOf(
    DummyDiary("아침 산책", "시원한 공기를 마시며 기분 좋은 하루를 시작했다."),
    DummyDiary("점심 회의", "새로운 기능에 대해 팀과 열띤 토론을 했다."),
    DummyDiary("저녁 독서", "오랜만에 소설을 읽으며 마음을 가라앉혔다.")
)
