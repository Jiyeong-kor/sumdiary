package com.jeong.sumdiary.domain.auth

data class UserSession(
    val id: String,
    val name: String,
    val token: String
)
