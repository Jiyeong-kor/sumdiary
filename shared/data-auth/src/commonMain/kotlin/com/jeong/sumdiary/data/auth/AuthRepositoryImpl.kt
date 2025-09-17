package com.jeong.sumdiary.data.auth

import com.jeong.sumdiary.domain.auth.AuthRepository
import kotlinx.coroutines.delay

class AuthRepositoryImpl : AuthRepository {
    override suspend fun currentUserId(): String? {
        delay(10)
        return null
    }
}
