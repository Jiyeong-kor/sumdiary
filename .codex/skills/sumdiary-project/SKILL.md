---
name: sumdiary-project
description: SumDiary 프로젝트에서 README, 문서, GitHub 이슈/PR, 빌드 검증, Android/iOS/KMP 작업을 할 때 사용하는 프로젝트 규칙. README 작성, 경고 없는 빌드, 한국어 커밋, 라벨, 개인정보/백업/온디바이스 AI 영향 검토가 필요한 모든 작업에서 사용한다.
---

# SumDiary Project

## Core Rules

- 사용자는 한국어를 선호한다. 이슈, PR, 커밋 제목/본문, README 설명은 한국어로 작성한다.
- 브랜치 이름에 `codex`를 붙이지 않는다.
- 작업은 이슈 먼저 만들고, 브랜치/PR은 이슈에 연결한다.
- 커밋은 Conventional Commits 형식을 사용하되 제목과 본문은 한국어로 쓴다.
- 커밋 본문에는 관련 이슈를 `Refs #번호`로 연결한다.
- 이슈와 PR에는 논리적으로 필요한 `type:*`, `area:*` 라벨을 붙인다.
- 사용자가 직접 만든 변경을 되돌리지 않는다.

## Local And IDE Files

- IDE가 만든 변경은 제품 코드와 섞지 말고 별도 판단한다.
- `.idea/gradle.xml`처럼 shared 모듈 인식에 필요한 안정적인 프로젝트 설정은 커밋할 수 있다.
- `.idea/deploymentTargetSelector.xml`처럼 개인 실행 대상, 기기 선택, 창 상태, 로컬 세션 값은 Git 추적에서 제외한다.
- `.idea` 변경이 남아 있으면 최종 보고 전에 공유 설정인지 로컬 설정인지 구분한다.

## Warning-Free Quality Gate

- `BUILD SUCCESSFUL`이어도 warning이 있으면 완료로 보지 않는다.
- 새 warning은 반드시 같은 작업 안에서 제거한다.
- 기존 warning을 발견하면 가능하면 함께 제거한다. 바로 제거하기 어렵다면 이슈를 만들고 PR 본문에 남은 warning, 원인, 후속 작업을 명확히 적는다.
- 최종 보고에서 “통과”라고 말하려면 실행한 명령과 warning 여부를 함께 말한다.
- Android/KMP 작업의 기본 검증은 다음을 우선한다:
  - `.\gradlew.bat :app:android:assembleDebug`
  - iOS/KMP 공용 코드 변경 시 관련 `compileKotlinIosSimulatorArm64`
  - 모듈 추가 시 해당 모듈의 `assembleDebug` 또는 metadata/iOS compile
- Gradle, Kotlin, Compose deprecation warning은 방치하지 않는다.
- 개인정보, 백업, 온디바이스 AI 관련 warning 또는 lint 성격의 문제는 출시 심사 리스크로 본다.

## README Structure

README는 “투자자/사용자용 홍보문”보다 “프로젝트를 이해하고 실행할 수 있는 제품 문서”로 작성한다.

권장 구조:

1. `# SumDiary`
   - 한 줄 설명: 온디바이스 AI로 일기를 요약하고, 사용자가 선택한 Google Drive에 암호화 백업하는 개인 일기 앱.
2. `## 제품 목표`
   - 개인정보 우선, 온디바이스 AI, 사용자가 켜는 백업, 한국 1차 출시 범위를 간결히 설명한다.
3. `## 핵심 기능`
   - 일기 작성/수정/삭제
   - 온디바이스 요약
   - Google Drive 암호화 백업/복구
   - 생체 인증과 앱 잠금
   - 첫 실행 가이드와 필수 고지
4. `## 개인정보와 보안 원칙`
   - 일기 원문은 요약을 위해 외부 서버로 보내지 않는다.
   - 백업은 사용자가 켠 뒤에만 동작한다.
   - 외부 클라우드에는 암호화된 백업 파일만 저장한다.
   - 백업 비밀번호 분실 시 복구할 수 없음을 명확히 쓴다.
5. `## 기술 구조`
   - Kotlin Multiplatform shared modules
   - Android Compose
   - iOS shared factory
   - SQLDelight
   - feature/domain/data 모듈 구조
6. `## 모듈 구성`
   - `app:android`
   - `app:ios`
   - `shared:domain-*`
   - `shared:data-*`
   - `shared:feature-*`
   - `shared:core-*`
7. `## 시작하기`
   - 요구사항
   - local properties 주의
   - Android debug build 명령
   - iOS/KMP compile 명령
8. `## 품질 기준`
   - warning-free build 원칙
   - CI 기준
   - 개인정보/백업/AI 변경 시 문서와 테스트를 함께 갱신한다.
9. `## GitHub Workflow`
   - 이슈 먼저
   - 브랜치 명명
   - 한국어 Conventional Commits
   - `Refs #번호`
   - PR 라벨
10. `## 출시 준비 상태`
   - 완료된 항목
   - 진행 중인 항목
   - 아직 출시 전인 항목을 솔직하게 구분한다.
11. `## 관련 문서`
   - `docs/PRD.md`
   - `docs/APP_IA.md`
   - `docs/UX_FLOW.md`
   - `docs/DESIGN_SYSTEM.md`
   - `docs/BACKUP_ARCHITECTURE.md`
   - `docs/DATA_MAP.md`
   - `docs/CI_PLAN.md`
   - `docs/GIT_WORKFLOW.md`

## README Writing Rules

- README에 실제 구현되지 않은 기능을 완료된 것처럼 쓰지 않는다.
- 아직 개발용 fake/in-memory 구현이면 “개발용”이라고 명확히 쓴다.
- 스토어 심사와 충돌할 수 있는 문구를 과장하지 않는다.
- “AI가 안전하게 처리합니다”처럼 범위가 모호한 표현 대신 “일기 원문을 요약 목적으로 외부 서버로 전송하지 않습니다”처럼 검증 가능한 문장으로 쓴다.
- 개인정보처리방침, 이용약관, Google Drive OAuth 심사 준비가 끝나기 전에는 출시 준비 완료라고 쓰지 않는다.
