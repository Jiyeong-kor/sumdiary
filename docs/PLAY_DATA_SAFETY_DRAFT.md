# Google Play Data safety draft

## Document status

- Status: release-candidate draft
- Basis: current SumDiary code and data map
- Final gate: verify against the live Play Console form, actual OAuth scopes,
  SDK list, network capture, and legal review before submission.

Google defines collection for the Data safety form as transmitting user data off
the user's device, including transmission by libraries or SDKs. SumDiary's draft
answers therefore distinguish local-only diary processing from optional
encrypted Google Drive backup.

Official reference:
https://support.google.com/googleplay/android-developer/answer/10787469

## Data collection summary

| Question | Draft answer | Evidence and caveat |
| --- | --- | --- |
| Does the app collect required user data types? | Yes, if the user enables Google Drive backup. | The app uploads an encrypted backup file to the user's Google Drive `appDataFolder`. |
| Is all collected user data encrypted in transit? | Yes. | Google Drive API uses HTTPS. Backup payload is encrypted before upload. Verify with network capture. |
| Can users request data deletion? | Yes. | Product requires local deletion and Google Drive backup file deletion. Verify final UI. |
| Is data collection optional? | Yes for Google Drive backup. | Local diary writing must work without cloud backup. |
| Is data shared? | No, based on current design. | User-selected Google Drive storage is used for app functionality, not sale, advertising, or third-party sharing. Re-check Google definitions during submission. |

## Data types

### Personal info

Draft answer: not collected by SumDiary servers.

Notes:
- SumDiary has no first-party account.
- Google account authorization is handled for Drive access.
- If provider account display name or email is stored or transmitted in a later
  implementation, add the relevant Personal info data type.

### App activity

Draft answer: not collected for analytics in the current release candidate.

Notes:
- No analytics SDK is currently committed as a release dependency.
- If analytics is added, disclose event categories and purposes.

### App info and performance

Draft answer: not collected by SumDiary in the current release candidate.

Notes:
- No crash reporting SDK is currently committed as a release dependency.
- If crash reporting is added, disclose diagnostics and verify payload filters.

### User content

Draft answer: collected only when the user enables Google Drive backup.

| Data | Purpose | Required? | Notes |
| --- | --- | --- | --- |
| Diary entries | App functionality, backup and restore | Optional | Uploaded only inside encrypted backup payload. |
| Summaries and emotion tags | App functionality, backup and restore | Optional | Uploaded only inside encrypted backup payload. |

### Files and docs

Draft answer: collected only when the user enables Google Drive backup.

| Data | Purpose | Required? | Notes |
| --- | --- | --- | --- |
| Encrypted backup file | App functionality, backup and restore | Optional | Stored in user-owned Google Drive app data folder. |

### Device or other IDs

Draft answer: not collected by SumDiary servers in the current release candidate.

Notes:
- If a device ID hash is included in encrypted backup payload, disclose it as
  optional app functionality data if Play Console classifies it as collected.

## Security practices

- Backup payload must be encrypted before upload.
- Android declares only the `INTERNET` permission for Google Drive OAuth/API
  communication in the current release candidate.
- OAuth tokens, backup password, raw encryption keys, and plaintext diary data
  must not be written into the backup file.
- Cloud AI fallback is disabled by default and must not be used without a new
  opt-in, policy update, and legal review.

## Pre-submission evidence checklist

- [ ] Play Console form screenshots saved.
- [ ] Google Drive OAuth scope verified as minimum necessary.
- [ ] Network capture shows no plaintext diary content.
- [ ] Backup fixture inspection shows no OAuth token, backup password, or raw key.
- [ ] Release build has no analytics/crash SDK unless disclosed.
- [ ] Privacy policy URL is public and matches these answers.
