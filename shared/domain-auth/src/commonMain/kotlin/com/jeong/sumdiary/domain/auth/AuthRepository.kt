package com.jeong.sumdiary.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isSignedIn: Flow<Boolean>

    suspend fun signIn(token: String)

    suspend fun signOut()
}
