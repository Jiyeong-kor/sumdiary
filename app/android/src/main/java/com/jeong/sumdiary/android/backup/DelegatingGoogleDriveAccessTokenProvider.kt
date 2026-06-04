package com.jeong.sumdiary.android.backup

import com.jeong.sumdiary.data.backup.GoogleDriveAccessToken
import com.jeong.sumdiary.data.backup.GoogleDriveAccessTokenProvider
import com.jeong.sumdiary.domain.backup.BackupCloudScope

class DelegatingGoogleDriveAccessTokenProvider : GoogleDriveAccessTokenProvider {
    private var delegate: GoogleDriveAccessTokenProvider? = null

    fun setDelegate(provider: GoogleDriveAccessTokenProvider) {
        delegate = provider
    }

    override suspend fun currentAccessToken(): GoogleDriveAccessToken? =
        delegate?.currentAccessToken()

    override suspend fun requestAccessToken(
        requiredScopes: Set<BackupCloudScope>
    ): GoogleDriveAccessToken? =
        delegate?.requestAccessToken(requiredScopes)
}
