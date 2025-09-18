package com.jeong.sumdiary.domain.auth

interface AuthRepository {
    suspend fun signIn(token: String)
    suspend fun signOut()
}
