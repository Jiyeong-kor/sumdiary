package com.jeong.sumdiary.data.diary

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.diary.db.SelectByDate
import com.jeong.sumdiary.data.diary.db.SelectRange
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DiaryRepositoryImpl(
    private val database: DiaryDatabase,
    private val dispatcher: CoroutineDispatcher
) : DiaryRepository {

    private val queries = database.diaryQueries

    override suspend fun upsert(entry: DiaryEntry) {
        withContext(dispatcher) {
            queries.upsertDiary(
                id = entry.id,
                date = entry.date.toString(),
                time = entry.time.toString(),
                content = entry.content
            )
        }
    }

    override suspend fun getByDate(date: LocalDate): List<DiaryEntry> = withContext(dispatcher) {
        queries.selectByDate(targetDate = date.toString())
            .executeAsList()
            .map { it.toDiaryEntry() }
    }

    override fun observeRange(from: LocalDate, to: LocalDate): Flow<List<DiaryEntry>> {
        return queries.selectRange(
            startDate = from.toString(),
            endDate = to.toString()
        ).asFlow().mapToList(dispatcher).map { rows -> rows.map { it.toDiaryEntry() } }
    }

    private fun SelectByDate.toDiaryEntry(): DiaryEntry =
        DiaryEntry(
            id = id,
            date = LocalDate.parse(date),
            time = LocalTime.parse(time),
            content = content
        )

    private fun SelectRange.toDiaryEntry(): DiaryEntry =
        DiaryEntry(
            id = id,
            date = LocalDate.parse(date),
            time = LocalTime.parse(time),
            content = content
        )
}
