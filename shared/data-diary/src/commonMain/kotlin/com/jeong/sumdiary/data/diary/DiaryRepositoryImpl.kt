package com.jeong.sumdiary.data.diary

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import com.jeong.sumdiary.core.model.DiaryEntryId
import com.jeong.sumdiary.data.diary.db.Diary
import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.data.diary.db.DiaryQueries
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DiaryRepositoryImpl(private val queries: DiaryQueries) : DiaryRepository {
    override suspend fun upsert(entry: DiaryEntry) {
        queries.upsert(
            id = entry.id.value,
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
        return queries.selectRange(
            date = from.toString(),
            date_ = to.toString(),
        ).asFlow().mapToList(Dispatchers.Default).map { rows ->
            rows.map { it.toDomain() }
        }
    }

    private fun Diary.toDomain(): DiaryEntry = DiaryEntry(
        id = DiaryEntryId(id),
        date = LocalDate.parse(date),
        time = LocalTime.parse(time),
        content = content,
    )

    companion object {
        fun create(driver: SqlDriver): DiaryRepositoryImpl {
            val database = DiaryDatabase(driver)
            return DiaryRepositoryImpl(database.diaryQueries)
        }
    }
}

fun createDiaryRepository(driver: SqlDriver): DiaryRepository = DiaryRepositoryImpl.create(driver)
