package com.jeong.sumdiary.data.diary

import com.jeong.sumdiary.data.diary.db.DiaryDatabase
import com.jeong.sumdiary.domain.diary.DiaryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object DiaryRepositoryFactory {
    fun create(
        database: DiaryDatabase,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
    ): DiaryRepository {
        return DiaryRepositoryImpl(database.diaryQueries, dispatcher)
    }
}
