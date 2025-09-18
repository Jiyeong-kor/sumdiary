package com.jeong.sumdiary.domain.auth

interface AuthRepository {
    suspend fun currentSession(): UserSession?
    suspend fun signOut()
}
