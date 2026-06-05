# Google OAuth verification audit

## Document status

- Status: release-candidate audit
- Basis: current SumDiary source code, Google Drive OAuth scope audit, backup
  architecture, and official Google OAuth/Drive documentation checked on
  2026-06-05
- Final gate: verify against the live Google Cloud OAuth consent screen,
  configured OAuth clients, final privacy policy URL, and tester/production
  publishing status before store submission.

## Current OAuth scope usage

SumDiary uses Google OAuth only for optional Google Drive backup. The current
release candidate requests the following Drive scope:

```text
https://www.googleapis.com/auth/drive.appdata
```

Current source evidence:

- Shared backup scope: `BackupCloudScope.GoogleDriveAppData`
- Shared repository required scopes: `GoogleDriveBackupCloudRepository.RequiredScopes`
- Android provider: `AndroidGoogleDriveAccessTokenProvider.GoogleDriveRequiredScopes`
- iOS provider: `GoogleDriveAuthorizationProvider.driveAppDataScope`

No broad Drive scopes such as `drive`, `drive.readonly`, `drive.metadata`, or
`drive.file` are required by the current backup flow.

## Official policy finding

Google's Drive application data folder guide states that apps must request the
`drive.appdata` scope before accessing the app data folder, and identifies that
scope as non-sensitive.

Google's Drive API scope guide lists Drive scopes by sensitivity. It identifies
`drive.appdata` as a recommended non-sensitive scope for viewing and managing
the app's own configuration data in the user's Google Drive. Broader Drive
scopes such as full Drive, read-only Drive, metadata, activity, and scripts are
restricted.

Google's OAuth app verification help states that apps requesting sensitive or
restricted scopes must complete OAuth app verification, while apps using only
non-sensitive scopes are not required to complete that sensitive/restricted
scope verification process.

Official references:

- https://developers.google.com/workspace/drive/api/guides/appdata
- https://developers.google.com/drive/api/guides/api-specific-auth
- https://support.google.com/cloud/answer/13463073
- https://developers.google.com/identity/protocols/oauth2/production-readiness/sensitive-scope-verification

## Current release conclusion

Based on the current code and official documentation, SumDiary's Google Drive
backup integration is not expected to require sensitive or restricted scope
verification because it requests only the non-sensitive `drive.appdata` scope.

This does not complete the whole OAuth release gate. The Google Cloud project
still needs a real OAuth consent screen, production publishing configuration,
Android/iOS OAuth client IDs, support/contact details, and a public privacy
policy URL. The live Cloud Console must be checked because Google groups scopes
and verification requirements in the actual project configuration.

## Required pre-submission evidence

- [ ] Google Cloud OAuth consent screen screenshots saved.
- [ ] OAuth consent screen publishing status recorded.
- [ ] Data Access page shows only `drive.appdata` plus any standard Sign-In
  scopes required by the final SDK behavior.
- [ ] Cloud Console does not classify the final requested scope set as
  sensitive or restricted.
- [ ] If Cloud Console marks any scope as sensitive or restricted, submit the
  required Google verification request before release.
- [ ] Privacy policy URL is public and matches the Google Drive backup behavior.
- [ ] Demo video or test procedure is prepared if Google requests verification
  evidence despite the non-sensitive scope set.
