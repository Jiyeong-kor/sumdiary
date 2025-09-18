package com.jeong.sumdiary.data.diary

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jeong.sumdiary.data.diary.db.Diary
import com.jeong.sumdiary.data.diary.db.DiaryQueries
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DiaryRepositoryImpl(
    private val queries: DiaryQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : DiaryRepository {

    override suspend fun upsert(entry: DiaryEntry) {
        queries.insertDiary(
            id = entry.id,
            date = entry.date.toString(),
            time = entry.time.toString(),
            content = entry.content,
        )
    }

    override suspend fun getByDate(date: LocalDate): List<DiaryEntry> {
        return queries.selectByDate(date = date.toString())
            .executeAsList()
            .map { it.toDomain() }
    }

    override fun observeRange(from: LocalDate, to: LocalDate): Flow<List<DiaryEntry>> {
        return queries.selectByRange(from = from.toString(), to = to.toString())
            .asFlow()
            .mapToList(dispatcher)
            .map { entities -> entities.map { it.toDomain() } }
    }

    private fun Diary.toDomain(): DiaryEntry {
        return DiaryEntry(
            id = id,
            date = LocalDate.parse(date),
            time = LocalTime.parse(time),
            content = content
        )
    }
}
