package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupCloudScope

data class GoogleDriveAccessToken(
    val value: String,
    val grantedScopes: Set<BackupCloudScope>
) {
    val hasRequiredScopes: Boolean =
        GoogleDriveBackupCloudRepository.RequiredScopes.all { it in grantedScopes }
}

interface GoogleDriveAccessTokenProvider {
    suspend fun currentAccessToken(): GoogleDriveAccessToken?
    suspend fun requestAccessToken(requiredScopes: Set<BackupCloudScope>): GoogleDriveAccessToken?
}

object UnavailableGoogleDriveAccessTokenProvider : GoogleDriveAccessTokenProvider {
    override suspend fun currentAccessToken(): GoogleDriveAccessToken? = null

    override suspend fun requestAccessToken(
        requiredScopes: Set<BackupCloudScope>
    ): GoogleDriveAccessToken? = null
}
