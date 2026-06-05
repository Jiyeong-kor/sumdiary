# SumDiary release configuration

## Android

Store submission builds require these Gradle properties. Put them in a local
`gradle.properties`, CI secret injection, or pass them on the command line.
Do not commit real values.

```properties
sumdiary.googleDriveOAuthClientId=<android-oauth-client-id>
sumdiary.android.signing.storeFile=<absolute-or-project-relative-keystore-path>
sumdiary.android.signing.storePassword=<store-password>
sumdiary.android.signing.keyAlias=<key-alias>
sumdiary.android.signing.keyPassword=<key-password>
```

Recommended local setup:

- Put the properties in `~/.gradle/gradle.properties` for local release checks.
- Keep the keystore outside the repository, or in a local ignored path only.
- Inject the same values through GitHub Actions secrets for CI release checks.
- Do not commit keystores, signing passwords, OAuth client secrets, `.env` files,
  `google-services.json`, or `GoogleService-Info.plist`.

The strict readiness audit requires all four signing properties and verifies
that `sumdiary.android.signing.storeFile` points to an existing file. This keeps
placeholder paths from being mistaken for release-ready signing evidence.

## iOS

Create `app/iosApp/Config/SumDiary.local.xcconfig` locally. Do not commit this
file. You can write it by hand:

```xcconfig
GOOGLE_DRIVE_IOS_CLIENT_ID = <ios-oauth-client-id>
GOOGLE_DRIVE_IOS_REVERSED_CLIENT_ID = <ios-reversed-client-id>
SUMDIARY_IOS_DEVELOPMENT_TEAM = <apple-developer-team-id>
```

Or generate it from the same Gradle properties used by the readiness audit:

```powershell
.\gradlew.bat writeIosReleaseLocalConfig `
  -Psumdiary.ios.googleDriveOAuthClientId=<ios-oauth-client-id> `
  -Psumdiary.ios.googleDriveOAuthReversedClientId=<ios-reversed-client-id> `
  -Psumdiary.ios.developmentTeam=<apple-developer-team-id>
```

Run the strict readiness audit with matching iOS values so the release evidence
records the same client identifiers used by Xcode.

```powershell
.\gradlew.bat checkReleaseReadiness `
  -Psumdiary.releaseReadiness.strict=true `
  -Psumdiary.googleDriveOAuthClientId=<android-oauth-client-id> `
  -Psumdiary.android.signing.storeFile=<keystore-path> `
  -Psumdiary.android.signing.storePassword=<store-password> `
  -Psumdiary.android.signing.keyAlias=<key-alias> `
  -Psumdiary.android.signing.keyPassword=<key-password> `
  -Psumdiary.ios.googleDriveOAuthClientId=<ios-oauth-client-id> `
  -Psumdiary.ios.googleDriveOAuthReversedClientId=<ios-reversed-client-id> `
  -Psumdiary.ios.developmentTeam=<apple-developer-team-id>
```

## Final local gates

```powershell
.\gradlew.bat :app:android:assembleDebug --warning-mode all
.\gradlew.bat :app:android:bundleRelease --warning-mode all
.\gradlew.bat :app:ios:compileKotlinIosSimulatorArm64 --warning-mode all
```

On macOS with Xcode installed:

```bash
xcodebuild \
  -project app/iosApp/SumDiary.xcodeproj \
  -target SumDiary \
  -configuration Debug \
  -sdk iphonesimulator \
  CODE_SIGNING_ALLOWED=NO \
  build
```
