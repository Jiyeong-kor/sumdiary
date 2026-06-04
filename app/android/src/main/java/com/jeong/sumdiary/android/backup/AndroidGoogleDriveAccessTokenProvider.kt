package com.jeong.sumdiary.android.backup

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.jeong.sumdiary.data.backup.GoogleDriveAccessToken
import com.jeong.sumdiary.data.backup.GoogleDriveAccessTokenProvider
import com.jeong.sumdiary.domain.backup.BackupCloudScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidGoogleDriveAccessTokenProvider(
    private val activity: ComponentActivity,
    private val authorizationLauncher: ActivityResultLauncher<IntentSenderRequest>
) : GoogleDriveAccessTokenProvider {
    private val authorizationClient = Identity.getAuthorizationClient(activity)
    private var pendingAuthorization: CompletableDeferred<GoogleDriveAccessToken?>? = null

    override suspend fun currentAccessToken(): GoogleDriveAccessToken? =
        authorize(requiredScopes = GoogleDriveRequiredScopes, launchResolution = false)

    override suspend fun requestAccessToken(
        requiredScopes: Set<BackupCloudScope>
    ): GoogleDriveAccessToken? =
        authorize(requiredScopes = requiredScopes, launchResolution = true)

    fun handleAuthorizationResult(data: Intent?) {
        val token = runCatching {
            authorizationClient.getAuthorizationResultFromIntent(data).toAccessToken()
        }.getOrNull()
        pendingAuthorization?.complete(token)
        pendingAuthorization = null
    }

    private suspend fun authorize(
        requiredScopes: Set<BackupCloudScope>,
        launchResolution: Boolean
    ): GoogleDriveAccessToken? {
        val request = AuthorizationRequest.builder()
            .setRequestedScopes(requiredScopes.map { Scope(it.value) })
            .build()

        val result = authorizationClient.authorize(request).awaitResult() ?: return null
        if (!result.hasResolution()) {
            return result.toAccessToken()
        }
        if (!launchResolution) {
            return null
        }

        val pendingIntent = result.pendingIntent ?: return null
        val deferred = CompletableDeferred<GoogleDriveAccessToken?>()
        pendingAuthorization = deferred
        authorizationLauncher.launch(
            IntentSenderRequest.Builder(pendingIntent.intentSender).build()
        )
        return deferred.await()
    }

    private fun AuthorizationResult.toAccessToken(): GoogleDriveAccessToken? {
        val token = accessToken ?: return null
        return GoogleDriveAccessToken(
            value = token,
            grantedScopes = grantedScopes
                .mapNotNull { scope -> BackupCloudScope.entries.firstOrNull { it.value == scope } }
                .toSet()
        )
    }

    private suspend fun <T> Task<T>.awaitResult(): T? =
        suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { result -> continuation.resume(result) }
            addOnFailureListener { continuation.resume(null) }
        }

    private companion object {
        val GoogleDriveRequiredScopes = setOf(BackupCloudScope.GoogleDriveAppData)
    }
}
