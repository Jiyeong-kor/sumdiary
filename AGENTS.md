# SumDiary Agent Rules

## 기본 응답

- 한국어로 짧고 압축해서 답한다.
- 사용자가 "normal mode", "stop caveman", "일반 모드"라고 말하기 전까지 caveman full 스타일을 유지한다.
- 코딩 작업은 기본적으로 `ponytail` 방식으로 한다. 가장 짧고 안전한 해결책부터 적용한다.
- 사용자가 직접 만든 변경은 되돌리지 않는다.

## 작업 경로

- 실제 작업 repo는 `C:\AndroidProject\JiYoung\sumdiary`로 우선 판단한다.
- `C:\Users\Jiyeong\Documents\Codex\2026-06-03\jiyeong-kor-sumdiary-https-github-com\work\sumdiary`는 예전 Codex worktree일 수 있다. 두 경로가 다르면 먼저 `git status`, 최근 커밋, dirty 상태를 비교한다.
- `AGENTS.md`가 있는 repo 루트를 기준으로 작업한다.

## iOS / Mac 금지

- 사용자는 Mac을 새로 사지 않는다. 중고 Mac도 사지 않는다. Mac 구매를 해결책으로 제안하지 않는다.
- Windows PC + GitHub Actions macOS runner + Sideloadly/AltStore 같은 무료 또는 저비용 루트를 우선한다.
- 현재 iPhone 실기기 실행 루트:
  1. GitHub Actions `iOS IPA Build`에서 unsigned IPA 생성
  2. Windows에서 artifact 다운로드
  3. Sideloadly로 무료 Apple ID 서명 및 설치
  4. iPhone에서 개발자 신뢰 후 실행
- 무료 Apple ID 사이드로드는 7일 재서명 제한이 있다. 이것을 숨기지 않는다.
- TestFlight, Ad Hoc, App Store 배포는 Apple Developer Program 유료 계정이 필요하다고 명확히 말한다.

## iOS 설치와 실행

- Sideloadly 경로 후보: `C:\Users\Jiyeong\AppData\Local\Sideloadly\sideloadly.exe`
- IPA 기본 다운로드 위치: `C:\AndroidProject\JiYoung\sumdiary\build\downloaded-ipa\SumDiary-unsigned.ipa`
- Sideloadly는 bundle id 뒤에 team id를 붙일 수 있다. 예: `com.jeong.sumdiary.dev.QZTGVQMGN7`
- 설치 확인은 `pymobiledevice3 apps query <bundle-id>` 또는 `pymobiledevice3 apps list`로 한다.
- 원격 launch는 iPhone 잠금 해제와 DeveloperDiskImage mount가 필요할 수 있다. `DeviceLocked`가 나오면 사용자가 iPhone 잠금을 풀고 화면을 켜야 한다.
- Sideloadly 실행 중 repo 루트에 `sideloadlydaemon.log`가 생기면 제품 코드로 취급하지 말고 삭제한다.

## iOS UI 주의

- 실기기 실행만 확인하려고 임시 SwiftUI 껍데기를 만들고 끝내지 않는다.
- 사용자가 iOS 앱 실행을 원하면 Android 화면과 다르게 보이는지 먼저 의심한다.
- 현재 Android 기본 화면은 `app/android/src/main/java/com/jeong/sumdiary/android/ui/SumDiaryScreen.kt`를 기준으로 한다.
- iOS wrapper는 최소한 Android의 현재 구조와 맞춘다: `일기`, `요약` 탭, 일기 리스트, `+` 작성 버튼, 새 일기 입력, 오늘/이번 주 요약 버튼.
- Figma 또는 4탭 IA(`일기`, `요약`, `백업`, `설정`) 작업이면 `sumdiary-figma-design-system` skill을 사용하고, 실제 Android/iOS 구현 차이를 보고한다.

## 빌드와 검증

- Android/KMP 작업은 warning-free 원칙을 따른다.
- iOS/KMP 공통 코드 변경 후 가능한 검증:
  - Windows: `.\gradlew.bat :app:ios:compileKotlinMetadata --warning-mode=all`
  - GitHub Actions: `iOS IPA Build`
- iOS device framework build는 Windows에서 직접 검증할 수 없다. GitHub Actions 결과로 확인한다.
- Actions 실패 시 로그의 첫 실제 컴파일 오류부터 고친다. 여러 오류가 이어서 드러날 수 있음을 사용자에게 짧게 말한다.

## 개인정보와 계정

- Apple ID, 비밀번호, 2FA 코드를 사용자 대신 입력하거나 노출하지 않는다.
- GitHub Secrets나 repo 파일에 Apple ID/비밀번호를 저장하지 않는다.
- IPA artifact에는 앱 코드가 들어가며 일기 데이터는 들어가지 않는다.
- public repo artifact 노출을 줄이려면 `actions/upload-artifact`에 `retention-days: 1`을 둔다.
- 공개 문서/PR/화면에는 사용자의 사적 전략, 계정 정보, 감정 기록, 약점 보완 의도를 남기지 않는다.

## Git

- 브랜치 이름에 `codex`를 붙이지 않는다.
- 커밋 메시지는 한국어 Conventional Commits 형식으로 쓴다.
- PR/issue가 필요한 작업은 가능하면 이슈 먼저 만든다. 단, 사용자가 즉시 실기기 실행처럼 흐름을 요청하면 현재 브랜치에 작게 커밋하고 진행한다.
