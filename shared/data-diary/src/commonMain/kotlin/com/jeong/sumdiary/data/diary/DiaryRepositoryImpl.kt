package com.jeong.sumdiary.data.diary

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jeong.sumdiary.domain.diary.model.DiaryEntry
import com.jeong.sumdiary.domain.diary.repository.DiaryRepository
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

    private val queries get() = database.diaryQueries

    override suspend fun upsert(entry: DiaryEntry) {
        withContext(dispatcher) {
            queries.upsert(
                id = entry.id,
                date = entry.date.toString(),
                time = entry.time.toString(),
                content = entry.content,
            )
        }
    }

    override suspend fun getByDate(date: LocalDate): List<DiaryEntry> = withContext(dispatcher) {
        queries.getByDate(date.toString())
            .executeAsList()
            .map { it.toDomain() }
    }

    override fun observeRange(from: LocalDate, to: LocalDate): Flow<List<DiaryEntry>> {
        return queries.getAllBetween(from.toString(), to.toString())
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomain() } }
    }

    private fun Diary.toDomain(): DiaryEntry {
        return DiaryEntry(
            id = id,
            date = LocalDate.parse(date),
            time = LocalTime.parse(time),
            content = content,
        )
    }
}
