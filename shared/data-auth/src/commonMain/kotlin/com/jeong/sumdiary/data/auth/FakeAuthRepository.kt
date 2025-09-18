package com.jeong.sumdiary.data.auth

import com.jeong.sumdiary.domain.auth.AuthRepository
import com.jeong.sumdiary.domain.auth.UserSession
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeAuthRepository : AuthRepository {
    private val mutex = Mutex()
    private var session: UserSession? = null

    override suspend fun currentSession(): UserSession? = mutex.withLock { session }

    override suspend fun signOut() {
        mutex.withLock { session = null }
    }

    suspend fun signIn(userSession: UserSession) {
        mutex.withLock { session = userSession }
    }
}
