package com.jeong.sumdiary.domain.auth

interface AuthRepository {
    suspend fun currentUserId(): String?
}
