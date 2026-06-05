# App Store App Privacy draft

## Document status

- Status: release-candidate draft
- Basis: current SumDiary code and data map
- Final gate: verify in App Store Connect against the actual SDK list, Google
  Drive configuration, Xcode archive, network capture, and legal review.

Apple requires developers to provide App Privacy details for data collected by
the app and integrated third-party code, including whether each data type is
linked to the user's identity or used for tracking.

Official references:
- https://developer.apple.com/app-store/app-privacy-details/
- https://developer.apple.com/help/app-store-connect/reference/app-information/app-privacy/

## Tracking

Draft answer: No.

Rationale:
- SumDiary does not use advertising identifiers.
- SumDiary does not combine user data with third-party data for advertising,
  measurement, or data broker purposes in the current release candidate.

## Data linked to the user

Draft answer: none for first-party SumDiary collection in the current release
candidate.

Caveat:
- If Google account email/display name is stored by the app or visible in local
  settings, reassess whether Contact Info or Identifiers are linked to the user.
- GoogleSignIn behavior and Google SDK disclosures must be checked before final
  submission.

## Data not linked to the user

Draft answer: disclose optional User Content if App Store Connect treats
user-selected encrypted cloud backup as collected by the app or third-party
partner.

| Data type | Draft purpose | Optional? | Notes |
| --- | --- | --- | --- |
| User Content: Other User Content | App functionality | Yes | Diary entries, summaries, and emotion tags are stored locally and may be uploaded only inside encrypted backup payload when the user enables Google Drive backup. |
| Diagnostics | Not currently collected | Not applicable | Add only if crash reporting SDK is introduced. |
| Usage Data | Not currently collected | Not applicable | Add only if analytics SDK is introduced. |

## Sensitive content notes

- Diary text can contain sensitive personal information, but the app does not
  transmit plaintext diary content to SumDiary servers or external AI servers.
- On-device AI summary providers process diary text locally.
- Unsupported devices must not fall back to cloud AI automatically.

## Pre-submission evidence checklist

- [ ] App Store Connect App Privacy screenshots saved.
- [ ] GoogleSignIn SDK privacy guidance checked for current version.
- [ ] Xcode archive built with final OAuth local xcconfig.
- [ ] Network capture shows no plaintext diary content.
- [ ] No crash/analytics SDK is included unless disclosed.
- [ ] Privacy policy URL is public and matches these answers.
