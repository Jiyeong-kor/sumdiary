package com.jeong.sumdiary.data.auth

import com.jeong.sumdiary.domain.auth.AuthRepository
import kotlinx.coroutines.delay

class AuthRepositoryImpl : AuthRepository {
    override suspend fun signIn(token: String) {
        delay(100)
    }

    override suspend fun signOut() {
        delay(50)
    }
}
