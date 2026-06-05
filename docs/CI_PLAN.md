# SumDiary CI Plan

## 1. 문서 정보

- 제품명: SumDiary
- 문서 목적: Android/iOS 동시 출시를 위한 CI 검증 단계와 GitHub Actions 도입 순서를 정의한다.
- 작성일: 2026-06-04
- 상태: 초안

## 2. CI 원칙

- 코드 또는 Gradle 변경 PR은 최소한 Gradle 빌드 검증을 통과해야 한다.
- 문서만 변경한 PR은 Android 빌드를 생략해 CI 시간을 줄인다.
- 문서 변경 PR의 링크와 기본 formatting 검토는 후속 lightweight docs workflow로 분리한다.
- 개인정보, 백업, AI 관련 변경은 테스트와 문서 업데이트를 함께 확인한다.
- Android와 shared KMP 검증을 자동화한다.
- iOS KMP framework compile은 macOS runner에서 검증한다.

## 3. 1차 CI 범위

1차로 GitHub Actions에 추가할 검증:

- Gradle wrapper 사용
- JDK 17 설정
- Gradle cache
- PR 중복 실행 취소
- pull_request에서는 cache read-only 사용
- master push에서만 cache write 허용
- Gradle cache cleanup 활성화
- 문서 전용 변경은 Android CI paths-ignore로 제외
- KMP metadata/common source compile
- Android debug build
- Android release build signing 전 단계 검증
- 백업 암호화 unit test
- iOS simulator arm64 framework compile
- iOS SwiftUI shell simulator build

현재 workflow 명령:

```text
./gradlew :app:android:assembleDebug --build-cache --parallel --stacktrace --warning-mode all
./gradlew :app:android:assembleRelease --build-cache --parallel --stacktrace --warning-mode all
./gradlew :app:android:bundleRelease --build-cache --parallel --stacktrace --warning-mode all
./gradlew :shared:data-backup:testDebugUnitTest --build-cache --parallel --stacktrace --warning-mode all
./gradlew :shared:data-summary:testDebugUnitTest --build-cache --parallel --stacktrace --warning-mode all
./gradlew checkReleaseReadiness --build-cache --parallel --stacktrace --warning-mode all
./gradlew :app:ios:compileKotlinIosSimulatorArm64 --build-cache --parallel --stacktrace --warning-mode all
xcodebuild -project app/iosApp/SumDiary.xcodeproj -target SumDiary -configuration Debug -sdk iphonesimulator CODE_SIGNING_ALLOWED=NO build
```

shared metadata compile은 전체 shared 모듈을 대상으로 실행한다.
스토어 제출 직전에는 다음 strict mode도 통과해야 한다.

```text
./gradlew writeIosReleaseLocalConfig \
  -Psumdiary.ios.googleDriveOAuthClientId=<ios-client-id> \
  -Psumdiary.ios.googleDriveOAuthReversedClientId=<ios-reversed-client-id> \
  -Psumdiary.ios.developmentTeam=<apple-developer-team-id>

./gradlew checkReleaseReadiness \
  -Psumdiary.releaseReadiness.strict=true \
  -Psumdiary.googleDriveOAuthClientId=<client-id> \
  -Psumdiary.android.signing.storeFile=<keystore-path> \
  -Psumdiary.android.signing.storePassword=<store-password> \
  -Psumdiary.android.signing.keyAlias=<key-alias> \
  -Psumdiary.android.signing.keyPassword=<key-password> \
  -Psumdiary.ios.googleDriveOAuthClientId=<ios-client-id> \
  -Psumdiary.ios.googleDriveOAuthReversedClientId=<ios-reversed-client-id> \
  -Psumdiary.ios.developmentTeam=<apple-developer-team-id>
```

`sumdiary.android.signing.storeFile`은 실제 존재하는 keystore 파일을 가리켜야 한다.
keystore, 서명 비밀번호, OAuth secret, `.env`, `google-services.json`,
`GoogleService-Info.plist`는 Git에 커밋하지 않고 로컬 설정 또는 CI secret으로만
주입한다.

## 3.1 캐시 최적화 전략

- `gradle/actions/setup-gradle`을 사용해 Gradle dependency cache, wrapper cache, local build cache를 관리한다.
- PR workflow는 `cache-read-only`로 실행해 불필요한 cache churn을 막는다.
- `master` push에서 cache를 갱신한다.
- `gradle-home-cache-cleanup`을 켜서 오래된 cache 항목으로 인한 용량 증가를 줄인다.
- workflow concurrency를 설정해 같은 브랜치의 이전 실행을 자동 취소한다.
- Gradle 실행에는 `--build-cache`와 `--parallel`을 사용한다.
- iOS/macOS CI는 비용과 속도 부담이 있으므로 PR에서는 KMP framework compile과 SwiftUI simulator build를 검증하고, SwiftUI 앱 archive 빌드는 별도 확장한다.

## 4. 2차 CI 범위

- ktlint 또는 spotless 도입 검토
- detekt 도입 검토
- shared 모듈 unit test 확대
- 백업 serialization/deserialization 테스트
- summary use case 테스트
- PR template 체크리스트
- Android lint
- docs link check
- privacy checklist job 분리

## 5. 3차 CI 범위

- iOS SwiftUI app archive build
- signed Android release bundle 검증
- Google Drive OAuth token provider 통합 테스트
- 스토어 제출 전 privacy checklist strict gate

## 6. PR 필수 체크

PR merge 전 확인:

- Android build 통과
- KMP shared compile 통과
- 관련 테스트 통과
- 개인정보/백업/AI 영향 문서 업데이트
- 관련 issue 연결

## 7. GitHub Actions 파일 계획

초기 파일:

```text
.github/workflows/android-ci.yml
```

후속 파일:

```text
.github/workflows/privacy-check.yml
```

## 8. 오픈 이슈

- Android lint를 첫 CI에 포함할지, 빌드 안정화 후 포함할지 결정한다.
- ktlint/detekt 중 어떤 정적 분석 도구를 사용할지 결정한다.
- iOS SwiftUI 앱 archive build CI 비용과 속도를 확인한다.
