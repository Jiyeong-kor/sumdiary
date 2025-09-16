package com.jeong.sumdiary.data.auth

import com.jeong.sumdiary.domain.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl : AuthRepository {
    private val signedIn = MutableStateFlow(false)

    override val isSignedIn: Flow<Boolean> = signedIn.asStateFlow()

    override suspend fun signIn(token: String) {
        if (token.isNotBlank()) {
            signedIn.value = true
        }
    }

    override suspend fun signOut() {
        signedIn.value = false
    }
}
