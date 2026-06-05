# Google Drive OAuth scope audit

## Status

- Date: 2026-06-05
- Scope: Android, iOS, and shared Google Drive backup implementation
- Result: SumDiary requests only `https://www.googleapis.com/auth/drive.appdata` for the first store submission scope.

## Required scope

| Scope | Purpose | Release decision |
| --- | --- | --- |
| `https://www.googleapis.com/auth/drive.appdata` | Store, read, replace, and delete the encrypted SumDiary backup file in the app-specific Google Drive data folder. | Approved for first release |

## Excluded scopes

SumDiary does not need broad Drive file browsing or user-visible file access for the first release.

| Scope family | Reason excluded |
| --- | --- |
| `https://www.googleapis.com/auth/drive` | Grants broad Drive access beyond the encrypted app backup file. |
| `https://www.googleapis.com/auth/drive.file` | Allows access to user-visible files created or opened by the app; not needed while using `appDataFolder`. |
| `https://www.googleapis.com/auth/drive.readonly` | Allows broader Drive reads than required for backup restore. |
| `https://www.googleapis.com/auth/drive.metadata*` | Broader metadata access is not needed because the app queries only its backup file in `appDataFolder`. |

## Implementation evidence

- Shared domain defines a single `BackupCloudScope.GoogleDriveAppData` value.
- Shared Google Drive repository requests only `GoogleDriveBackupCloudRepository.RequiredScopes`, which is `GoogleDriveAppData`.
- Android `AndroidGoogleDriveAccessTokenProvider` maps requested shared scopes to Google Identity Services `Scope` values and uses the shared required scope for current-token checks.
- iOS `GoogleDriveAuthorizationProvider.driveAppDataScope` is the same `drive.appdata` URL and `requestAccessToken` defaults to that single scope.
- Google Drive API upload metadata uses parent `appDataFolder`.
- Google Drive API list queries use `spaces=appDataFolder` and only search `sumdiary-backup.json`.

## Regression guard

`GoogleDriveBackupCloudRepositoryTest.connectRequestsOnlyGoogleDriveAppDataScope` verifies that the repository requests only `GoogleDriveAppData` and that no additional shared backup cloud scope is currently defined.

## Remaining submission evidence

- Capture the actual OAuth consent screen before store submission.
- Confirm in Google Cloud Console that the Android and iOS OAuth clients list only the approved Drive app data scope.
- Keep real OAuth client IDs and signing values in local configuration or CI secrets only.
