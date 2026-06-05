# SumDiary

온디바이스 AI로 일기를 요약하고, 사용자가 선택한 Google Drive에 암호화 백업하는 개인 일기 앱입니다.

SumDiary는 Android와 iOS 동시 출시를 목표로 하는 Kotlin Multiplatform 프로젝트입니다. 1차 출시는 대한민국을 대상으로 하며, 개인정보 보호와 사용자의 명시적 선택을 제품의 기본 원칙으로 둡니다.

## 제품 목표

- 일기 작성, 수정, 삭제를 빠르고 조용한 흐름으로 제공한다.
- 일기 요약은 기본적으로 온디바이스 AI로 처리한다.
- 일기 원문은 요약을 위해 외부 AI 서버로 전송하지 않는다.
- 클라우드 백업은 사용자가 직접 켠 경우에만 동작한다.
- SumDiary 자체 계정과 자체 백업 서버는 1차 출시 범위에 포함하지 않는다.
- 외부 클라우드에는 앱에서 암호화한 백업 파일만 저장한다.

## 핵심 기능

- 일기 작성, 목록 확인, 수정, 삭제 확인
- 일간/주간 요약 상태 표현
- 온디바이스 요약 엔진 지원 여부 경계
- 첫 실행 앱 가이드와 필수 개인정보/AI/백업 고지
- Google Drive 암호화 백업 도메인 경계
- 백업 설정 상태 UI와 백업 비밀번호 입력 UX
- Android 디자인 시스템 토큰과 기본 화면 적용

## 개인정보와 보안 원칙

- 일기 원문은 사용자의 기기에 저장되는 민감 데이터로 취급한다.
- 온디바이스 요약이 지원되지 않는 기기에서는 자동 클라우드 AI fallback을 실행하지 않는다.
- Google Drive 백업은 사용자가 직접 연결하고 실행한 경우에만 동작해야 한다.
- 백업 파일에는 OAuth access token, refresh token, 백업 비밀번호를 포함하지 않는다.
- 백업 비밀번호를 잃어버리면 SumDiary도 백업을 복구할 수 없다.
- Face ID, Touch ID, Android 생체 인증은 편의 기능이며 새 기기 복구 수단을 대체하지 않는다.

## 기술 구조

- Kotlin Multiplatform 기반 shared 모듈
- Android Jetpack Compose UI
- iOS shared factory 구조
- SQLDelight 기반 로컬 저장소
- feature/domain/data/core 모듈 분리
- GitHub Actions 기반 Android/iOS KMP CI

## 모듈 구성

| 모듈 | 역할 |
| --- | --- |
| `app:android` | Android Compose 앱 |
| `app:ios` | iOS 앱 진입 factory |
| `shared:core-model` | 공용 모델 |
| `shared:core-util` | 공용 유틸리티와 dispatcher |
| `shared:core-designsystem` | 디자인 시스템 토큰 |
| `shared:domain-diary` | 일기 도메인 모델과 repository 계약 |
| `shared:data-diary` | 일기 SQLDelight 저장소 구현 |
| `shared:domain-summary` | 요약 도메인 모델과 use case |
| `shared:data-summary` | 요약 저장소와 summarizer engine 경계 |
| `shared:feature-entry` | 일기 작성/목록 상태 관리 |
| `shared:feature-summary` | 요약 화면 상태 관리 |
| `shared:domain-backup` | 백업 도메인 모델, 암호화 파일, provider 계약 |
| `shared:data-backup` | 백업 repository, 암호화 구현, SQLDelight snapshot, Google Drive appDataFolder API client |
| `shared:feature-backup` | 백업 설정 화면 상태와 intent |
| `shared:domain-auth` | 인증/보안 도메인 계약 |
| `shared:data-auth` | 인증/보안 data 구현 |

## 시작하기

### 요구사항

- JDK 17
- Android Studio 또는 Gradle wrapper 실행 환경
- Android SDK 36
- iOS/KMP 작업 시 Kotlin Native toolchain이 동작하는 환경

### Android debug build

```powershell
.\gradlew.bat :app:android:assembleDebug
```

### iOS/KMP shared compile 예시

```powershell
.\gradlew.bat :shared:feature-backup:compileKotlinIosSimulatorArm64
```

### CI

Android/iOS KMP CI는 `.github/workflows/android-ci.yml`에서 관리합니다.

## 품질 기준

- `BUILD SUCCESSFUL`이어도 warning이 있으면 완료로 보지 않는다.
- 새 warning은 같은 작업 안에서 제거한다.
- Gradle, Kotlin, Compose deprecation warning은 방치하지 않는다.
- 개인정보, 백업, 온디바이스 AI 관련 변경은 문서와 검증 결과를 함께 갱신한다.
- 문서만 변경한 PR은 빌드를 생략할 수 있지만, 코드/Gradle 변경 PR은 관련 Gradle 검증을 수행한다.

## GitHub Workflow

- 모든 작업은 이슈를 먼저 만든다.
- 브랜치와 PR은 관련 이슈에 연결한다.
- 브랜치 이름에 `codex`를 붙이지 않는다.
- 커밋은 한국어 Conventional Commits 형식을 사용한다.
- 커밋 본문에는 `Refs #번호`로 관련 이슈를 연결한다.
- 이슈와 PR에는 필요한 `type:*`, `area:*` 라벨을 붙인다.

예시:

```text
feat(backup): 백업 비밀번호 입력 UX 연결

Refs #27
```

## 출시 준비 상태

### 완료됨

- 제품 요구사항, IA, UX flow, 디자인 시스템 문서화
- Android 기본 화면과 디자인 시스템 적용
- 첫 실행 앱 가이드와 필수 고지
- 일기 작성/수정/삭제 확인 흐름
- 요약 상태 UX와 온디바이스 요약 엔진 지원 여부 경계
- Google Drive 암호화 백업 도메인 경계
- SQLDelight 기반 백업 snapshot과 Android 백업 암호화 구현
- Google Drive appDataFolder 업로드/다운로드/삭제 API client
- Android Google Drive OAuth access token provider
- Android ML Kit GenAI 요약 provider
- Android 생체/기기 인증 앱 잠금 gate
- iOS Apple Foundation Models 요약 provider 주입 경계와 Swift provider
- iOS Face ID/Touch ID/기기 암호 앱 잠금 gate와 Face ID 사용 문구
- iOS GoogleSignIn 기반 Google Drive OAuth provider와 URL callback hook
- 백업 설정 상태 UI와 백업 비밀번호 입력 UX
- README 구조와 warning-free 프로젝트 규칙

### 진행 중

- Android/iOS 온디바이스 AI 지원 기기와 미지원 기기 UX 실기기 검증
- iOS Xcode 26 이상 archive 검증
- Android/iOS 생체 인증 앱 잠금 실기기 검증
- 백업 키 접근 승인
- 복구 방식 선택과 충돌 처리 UX

### 출시 전 필요

- 개인정보처리방침
- 이용약관
- Android release signing 설정
- Android Google Drive OAuth client id Gradle property 설정
- iOS `app/iosApp/Config/SumDiary.local.xcconfig`에 Google Drive OAuth client id와 reversed URL scheme 설정
- Google Drive OAuth scope 검토와 필요 시 Google 검증
- App Store / Google Play privacy form과 data safety 답변
- 실제 기기 테스트와 미지원 기기 UX 검증
- Android와 iOS 양쪽 출시 빌드 검증

## 관련 문서

- [PRD](docs/PRD.md)
- [App IA](docs/APP_IA.md)
- [UX Flow](docs/UX_FLOW.md)
- [Design System](docs/DESIGN_SYSTEM.md)
- [Backup Architecture](docs/BACKUP_ARCHITECTURE.md)
- [Data Map](docs/DATA_MAP.md)
- [CI Plan](docs/CI_PLAN.md)
- [Git Workflow](docs/GIT_WORKFLOW.md)
- [UI Delivery Plan](docs/UI_DELIVERY_PLAN.md)
- [Release Configuration](docs/RELEASE_CONFIGURATION.md)
- [Privacy Policy](docs/PRIVACY_POLICY.md)
- [Terms of Use](docs/TERMS_OF_USE.md)
- [Store Submission Checklist](docs/STORE_SUBMISSION_CHECKLIST.md)
- [Google Play Data Safety Draft](docs/PLAY_DATA_SAFETY_DRAFT.md)
- [App Store Privacy Draft](docs/APP_STORE_PRIVACY_DRAFT.md)
- [Store Content Rating Draft](docs/CONTENT_RATING_DRAFT.md)
- [Google OAuth Verification Audit](docs/GOOGLE_OAUTH_VERIFICATION_AUDIT.md)
