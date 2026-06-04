# SumDiary UI Delivery Plan

## 1. 문서 정보

- 제품명: SumDiary
- 문서 목적: 앱 IA 이후 UX 정의, 디자인 시스템 구축, UI 구현의 순서를 정의한다.
- 작성일: 2026-06-04
- 상태: 초안

## 2. 작업 순서

```text
PRD
  -> APP IA
  -> UX Flow
  -> Design System
  -> Android UI
  -> iOS UI
  -> QA / Accessibility
```

UI 구현은 IA와 UX flow가 정리된 뒤 시작한다.

## 3. UX 정의 산출물

UI 구현 전 다음 문서를 만든다.

- 화면별 state 정의
- 화면별 empty/loading/error 상태
- 주요 flow별 wireframe
- 개인정보/백업/AI 고지 문구
- 첫 실행 앱 가이드 문구
- 앱 가이드 건너뛰기 UX
- 삭제/복구/연결 해제 확인 문구
- 햅틱 피드백 적용 지점
- Android/iOS platform behavior 차이

권장 문서:

```text
docs/UX_FLOW.md
```

## 4. 디자인 시스템 산출물

UX flow 이후 다음을 정의한다.

- Color token
- Typography token
- Spacing token
- Shape token
- Icon policy
- Button
- Text field
- Dialog
- Bottom sheet
- Tab
- List item
- Empty state
- Loading state
- Error state
- Sensitive action confirmation
- Haptic feedback policy

권장 문서:

```text
docs/DESIGN_SYSTEM.md
```

## 5. 구현 원칙

- Android는 Compose로 구현한다.
- iOS는 SwiftUI로 구현한다.
- shared feature state는 KMP에서 최대한 공유한다.
- UI component는 플랫폼별로 구현하되 용어, 상태, flow는 동일하게 유지한다.
- 개인정보/백업/삭제 화면은 구현 전 문구 검토를 완료한다.

## 6. 구현 우선순위

### P0

- 일기 탭 IA 반영
- 요약 탭 IA 반영
- 설정 탭 추가
- 첫 실행 앱 가이드
- 설정의 앱 가이드 다시 보기
- 백업 설정 진입점
- 개인정보/AI 고지 화면
- 생체 인증 설정 진입점
- 햅틱 피드백 설정

### P1

- Google Drive 연결 화면
- 백업 비밀번호 설정 화면
- 백업 실행/복구 상태 화면
- 백업 파일 삭제 확인 화면
- 외부 클라우드 연결 해제 화면

### P2

- 고급 복구 문구 옵션
- 상세 백업 기록
- provider 확장 UI
