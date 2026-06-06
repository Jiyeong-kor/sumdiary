# SumDiary Design System

## 1. 문서 정보

- 제품명: SumDiary
- 문서 목적: Android Compose와 iOS SwiftUI UI 구현 전에 공통 브랜드 방향, 디자인 토큰, 컴포넌트 정책, 상태 표현 원칙을 정의한다.
- 작성일: 2026-06-04
- 상태: 디자인 패키지 적용 기준
- 관련 문서: `docs/APP_IA.md`, `docs/UX_FLOW.md`, `docs/UI_DELIVERY_PLAN.md`
- 관련 이슈: Refs #11, Refs #90
- 적용 패키지: `C:/Users/Jiyeong/Downloads/sumdiary_design_system_package/sumdiary_design_system_package`

## 2. 디자인 방향

SumDiary는 깔끔한 생산성 도구의 밀도 위에 수채화 블루베리 패턴의 부드러운 질감을 얹어 설계한다.
기록과 요약은 빠르고 정돈되어야 하며, 브랜드 감성은 배경 질감, 작은 일러스트, primary action에서만 드러낸다.

핵심 인상:

- 개인 기록을 빠르게 남기고 요약하는 도구
- 밝은 paper surface와 블루 primary 중심의 차분한 화면
- 수채화 블루베리 모티프를 빈 상태와 안내 화면에 제한적으로 사용
- 은은한 종이 질감과 둥근 outline 컴포넌트
- 정보가 한눈에 보이는 밀도
- 짧고 미니멀한 한국어 문장

브랜드 키워드:

- 정돈된
- 사적인
- 신뢰할 수 있는
- 빠른
- 수채화 질감
- 차분한 생산성

금지 방향:

- AI 앱처럼 보이는 보라/파랑 네온 그라데이션
- 웰니스 앱처럼 과하게 부드러운 파스텔
- 감성 문구가 많은 일기장 톤
- 과한 일러스트 중심 온보딩
- 카드 안에 카드가 반복되는 복잡한 프레임
- 법적 고지를 기능 소개처럼 작게 숨기는 표현

## 3. 브랜드 상징

SumDiary의 핵심 상징은 `watercolor berry lens`와 `정리되는 문장`이다.
브랜드 마크는 블루베리 점 패턴이 렌즈처럼 모여 짧은 기록을 정리하는 이미지를 만든다.

로고와 아이콘 방향:

- 긴 문장이 짧게 정리되는 형태
- 텍스트 블록 위를 통과하는 블루베리 렌즈 또는 초점
- 여러 줄이 한 줄의 요약으로 수렴하는 구조
- 잠금, 방패, 클라우드 같은 직접 상징은 보조 아이콘에서만 사용

패키지 에셋 사용:

- `brand_mark_blueberry_lens.png`: 앱 가이드, 브랜드 보조 표식
- `empty_diary.png`: 빈 일기 상태
- `backup_cloud.png`: 백업 설정 상태
- `delete_data.png`: 삭제 확인
- `biometric_lock.png`: 앱 잠금
- `watercolor_berry_pattern_tile.png`, `paper_grain_texture.png`: 배경/문서 시안용 질감

아이콘 금지:

- 흔한 AI 반짝이 아이콘을 브랜드 핵심으로 사용하지 않는다.
- 보안 화면 전체를 방패 아이콘 중심으로 만들지 않는다.
- 일기 화면에서 자물쇠를 과하게 반복하지 않는다.

## 4. 색상 토큰

색상은 라이트 모드와 다크 모드를 1차 출시부터 함께 정의한다.

원칙:

- 밝은 paper surface와 블루 primary를 기본으로 한다.
- 보조 블루는 일러스트, 활성 border, 선택 배경처럼 구조를 보조할 때만 사용한다.
- 순수 검정 `#000000`은 사용하지 않는다.
- 개인정보, 삭제, 오류 등 법적/위험 맥락은 브랜드 포인트와 분리한다.
- 배경에는 아주 약한 종이 질감 또는 매트 질감을 허용한다.

### 4.1 라이트 모드

| Token | Hex | 역할 |
| --- | --- | --- |
| `color.background` | `#FAFAFA` | 기본 화면 배경 |
| `color.backgroundPaper` | `#F7F6F2` | 종이 질감 배경 보조색 |
| `color.surface` | `#FFFFFF` | 입력 영역, 모달, 주요 패널 |
| `color.surfaceSubtle` | `#F5F7FA` | 보조 패널, 구분 영역 |
| `color.surfacePressed` | `#EEF2FA` | 눌림 상태 |
| `color.textPrimary` | `#1E1E1E` | 본문 주요 텍스트 |
| `color.textSecondary` | `#686868` | 보조 설명, 메타 정보 |
| `color.textTertiary` | `#9A9A9A` | 비활성 설명, placeholder |
| `color.border` | `#E5E7EB` | 기본 hairline, divider |
| `color.borderStrong` | `#A9C0F7` | 활성 border, 구조 강조 |
| `color.accent` | `#4F7CF3` | 주요 액션, 선택 상태, focus |
| `color.accentSubtle` | `#DDE8FF` | 선택 배경, 가벼운 강조 |
| `color.secondary` | `#A9C0F7` | 보조 아이콘, 일러스트 강조 |
| `color.secondarySubtle` | `#EDF3FF` | 보조 강조 배경 |
| `color.warning` | `#D9A441` | 백업/복구 주의 |
| `color.warningSubtle` | `#FFF4D6` | 주의 배경 |
| `color.danger` | `#E64B4B` | 삭제, 위험 액션 |
| `color.dangerSubtle` | `#FFE4E4` | 위험 배경 |
| `color.success` | `#4CAF7B` | 저장/백업 성공 |
| `color.info` | `#4F7CF3` | 설명, AI 지원 정보 |

### 4.2 다크 모드

| Token | Hex | 역할 |
| --- | --- | --- |
| `color.background` | `#111317` | 기본 화면 배경 |
| `color.surface` | `#1B1D22` | 입력 영역, 모달, 주요 패널 |
| `color.surfaceSubtle` | `#242832` | 보조 패널, 구분 영역 |
| `color.surfacePressed` | `#2A2E38` | 눌림 상태 |
| `color.textPrimary` | `#F4F5F7` | 본문 주요 텍스트 |
| `color.textSecondary` | `#C4C7CE` | 보조 설명, 메타 정보 |
| `color.textTertiary` | `#8E929B` | 비활성 설명, placeholder |
| `color.border` | `#343844` | 기본 hairline, divider |
| `color.borderStrong` | `#5B668A` | 활성 border, 구조 강조 |
| `color.accent` | `#9AB5FF` | 주요 액션, 선택 상태, focus |
| `color.accentSubtle` | `#263A70` | 선택 배경, 가벼운 강조 |
| `color.secondary` | `#A9C0F7` | 보조 아이콘, 일러스트 강조 |
| `color.warning` | `#E0B85F` | 백업/복구 주의 |
| `color.warningSubtle` | `#3D3218` | 주의 배경 |
| `color.danger` | `#FF8B86` | 삭제, 위험 액션 |
| `color.dangerSubtle` | `#4A2427` | 위험 배경 |
| `color.success` | `#80C79F` | 저장/백업 성공 |
| `color.info` | `#9AB5FF` | 설명, AI 지원 정보 |

### 4.3 표면 질감

종이 또는 매트 질감은 배경에만 아주 약하게 적용한다.

- 라이트 모드: 2% 이하 노이즈 또는 미세한 paper grain
- 다크 모드: 3% 이하 노이즈 또는 매트 카본 질감
- 텍스트, 입력창, 리스트 행 위에 직접 노이즈를 얹지 않는다.
- 애니메이션되는 노이즈는 사용하지 않는다.

## 5. Typography

한국어 가독성을 우선한다.

권장 폰트:

- Android: `Noto Sans KR`, 시스템 기본 sans fallback
- iOS: `Apple SD Gothic Neo`, 시스템 기본 sans fallback
- 디자인 시안: `Pretendard` 또는 `SUIT`

숫자와 시간:

- 날짜, 시간, 카운트, 백업 파일 크기는 tabular number를 적용한다.
- Android Compose에서는 `FontFeature.Settings("tnum")` 적용을 검토한다.
- iOS SwiftUI에서는 monospaced digit modifier 사용을 검토한다.

### 5.1 Type scale

| Token | Size | Line height | Weight | 용도 |
| --- | ---: | ---: | ---: | --- |
| `type.display` | 28 | 36 | 700 | 온보딩 핵심 문장, 빈 상태 대표 문장 |
| `type.headlineLarge` | 24 | 32 | 700 | 주요 화면 제목 |
| `type.headlineMedium` | 20 | 28 | 700 | 상단 바/강조 섹션 제목 |
| `type.titleLarge` | 18 | 26 | 600 | 섹션 제목, 상세 제목 |
| `type.bodyLarge` | 16 | 24 | 400 | 입력 본문, 주요 설명 |
| `type.bodyMedium` | 14 | 22 | 400 | 리스트 설명, 보조 본문 |
| `type.labelLarge` | 14 | 20 | 600 | 버튼, 탭 라벨 |
| `type.labelMedium` | 12 | 18 | 500 | 메타 정보, 상태 라벨 |
| `type.caption` | 11 | 16 | 500 | 법적 보조 문구, 시간 |

원칙:

- 앱 안에서 과도한 대형 헤드라인을 사용하지 않는다.
- 문장은 짧게 유지한다.
- 법적 고지와 도움말은 읽기 편한 line height를 유지한다.
- 주요 화면의 본문 폭은 지나치게 길어지지 않게 한다.

## 6. Spacing

4pt 기반 spacing scale을 사용한다.

| Token | Value | 용도 |
| --- | ---: | --- |
| `space.0` | 0 | 붙임 |
| `space.xxs` | 4 | 작은 indicator, 아이콘과 텍스트 최소 간격 |
| `space.xs` | 8 | 작은 내부 간격, 리스트 간격 |
| `space.sm` | 12 | 행 내부 간격, 카드 gap |
| `space.md` | 16 | 기본 내부 간격 |
| `space.lg` | 20 | 카드/패널 padding |
| `space.xl` | 24 | 화면 gutter, 상단 여백 |
| `space.xxl` | 32 | 큰 섹션 간격 |
| `space.section` | 40 | 온보딩/주요 섹션 간격 |

화면 밀도:

- 정보는 한눈에 보이게 배치하되 터치 영역은 줄이지 않는다.
- 리스트 행의 최소 높이는 56pt 이상으로 유지한다.
- 주요 액션 버튼의 최소 높이는 48pt 이상으로 유지한다.
- 설정 화면은 카드보다 divider와 group label 중심으로 구성한다.

## 7. Shape

| Token | Value | 용도 |
| --- | ---: | --- |
| `radius.xs` | 4 | 작은 badge, 내부 indicator |
| `radius.sm` | 8 | 리스트 행, 입력창 |
| `radius.md` | 12 | 버튼, 작은 패널 |
| `radius.lg` | 16 | 카드, dialog |
| `radius.xl` | 20 | 큰 안내 패널 |
| `radius.xxl` | 24 | 큰 modal surface |
| `radius.sheet` | 28 | bottom sheet |
| `radius.full` | 999 | switch thumb, progress pill |

원칙:

- 전반적으로 둥근 앱보다 정돈된 앱에 가깝게 유지한다.
- 반복 카드에는 `radius.sm` 또는 `radius.md`까지만 사용한다.
- dialog와 bottom sheet는 플랫폼 관습을 따른다.

## 8. Elevation

SumDiary는 무거운 그림자를 사용하지 않는다.

| Token | 역할 |
| --- | --- |
| `elevation.none` | 일반 리스트, 설정 group |
| `elevation.low` | 입력 패널, 요약 결과 |
| `elevation.modal` | dialog, bottom sheet |

표현 원칙:

- 구분은 shadow보다 배경색, border, spacing으로 처리한다.
- 다크 모드에서 shadow는 거의 쓰지 않고 border와 surface 차이를 사용한다.
- floating action button을 쓰더라도 그림자는 약하게 유지한다.

## 9. Iconography

아이콘은 기능 이해를 돕는 보조 요소로 사용한다.

공통 원칙:

- stroke 기반, 1.75pt 또는 2pt stroke를 기준으로 한다.
- Android는 Material Symbols 또는 Compose Material icon을 우선한다.
- iOS는 SF Symbols를 우선한다.
- 동일 기능은 플랫폼이 달라도 같은 은유를 유지한다.
- 아이콘만으로 법적 의미를 전달하지 않는다.

주요 은유:

- 일기 작성: edit, note
- 요약: lines reducing, lens, text search
- 백업: drive, upload, archive
- 복구: restore, history
- 생체 인증: fingerprint, face id platform symbol
- 개인정보: document lock, privacy
- 삭제: trash

## 10. Component Policy

### 10.1 Button

버튼 계층:

| 종류 | 역할 |
| --- | --- |
| Primary | 화면의 다음 단계 또는 저장 |
| Secondary | 보조 경로 |
| Tertiary | 작고 조용한 링크 액션 |
| Destructive | 삭제, 연결 해제, 전체 데이터 삭제 |

원칙:

- 한 화면에 primary는 하나만 둔다.
- `건너뛰기`는 tertiary로 작게 제공한다.
- 위험 액션은 primary 색을 사용하지 않는다.
- 버튼 텍스트는 2~6어절 이내로 유지한다.

상태:

- enabled
- pressed
- loading
- disabled
- destructive

### 10.2 Text field

일기 입력은 생산성 도구처럼 빠르게 열리고 빠르게 저장되어야 한다.

원칙:

- label은 입력창 위에 둔다.
- placeholder는 짧게 쓴다.
- 일기 본문 입력은 `bodyLarge`를 사용한다.
- 긴 일기 입력에서도 저장 버튼이 접근 가능해야 한다.
- 입력 실패 시 사용자가 쓴 내용은 유지한다.
- 원문 내용을 로그나 오류 문구에 노출하지 않는다.

### 10.3 List item

일기 목록은 정보 밀도를 높이되 조용하게 읽히게 한다.

일기 row 구성:

- 시간
- 첫 줄 또는 요약 preview
- 수정 상태 또는 백업 상태가 필요한 경우 작은 meta

원칙:

- 기본 row에는 아이콘을 과하게 넣지 않는다.
- 날짜와 시간은 tabular number를 사용한다.
- 삭제, 수정 등 secondary action은 상세 또는 context menu에서 제공한다.

### 10.4 Summary card

요약 결과는 카드처럼 보일 수 있으나, 과도하게 감성적인 quote card로 만들지 않는다.

구성:

- 기간
- 요약 상태
- 요약문
- 감정 태그 또는 키워드
- 재생성/공유 등 액션은 정책 확정 전까지 최소화

원칙:

- 요약은 사실 확정이 아니라 회고 보조임을 표현한다.
- AI가 만든 내용이라는 맥락은 숨기지 않는다.
- 감정 태그는 보조 정보로 낮은 대비를 사용한다.

### 10.5 Dialog

Dialog는 중요한 확인에만 사용한다.

사용:

- 일기 삭제
- 전체 데이터 삭제
- 백업 파일 삭제
- 외부 클라우드 연결 해제
- 복구 방식 선택 전 확인

원칙:

- 제목은 짧고 구체적으로 쓴다.
- 본문은 결과와 되돌릴 수 있는지 여부를 설명한다.
- 위험 액션 버튼은 오른쪽 또는 플랫폼 관습상 강조 위치에 두되 danger 색을 사용한다.
- 법적 동의는 단순 dialog로 처리하지 않는다.

### 10.6 Bottom sheet

Bottom sheet는 맥락 유지가 중요한 선택에 사용한다.

사용:

- 날짜 선택
- 일기 작성 방식이 sheet로 확정된 경우
- 백업 설명의 짧은 보충 정보
- 감정 태그 표시 옵션

원칙:

- sheet 안에서 긴 법적 문서를 읽게 하지 않는다.
- 위험 액션은 sheet 하단에 몰아넣지 말고 별도 확인 흐름을 둔다.

### 10.7 Tabs

최상위 탭:

- 일기
- 요약
- 설정

원칙:

- 탭 전환에 햅틱을 적용하지 않는다.
- 현재 탭은 accent 또는 textPrimary로만 차분하게 표시한다.
- badge는 꼭 필요한 상태에만 사용한다.

## 11. State Design

모든 주요 화면은 다음 상태를 가진다.

- loading
- empty
- content
- error
- unsupported
- permission needed

### 11.1 Loading

- 원형 spinner보다 skeleton 또는 작은 progress를 우선한다.
- 일기 원문처럼 민감한 정보 형태를 추측할 수 있는 skeleton은 피한다.
- 요약 생성은 시간이 걸릴 수 있으므로 진행 중 문구를 제공한다.

### 11.2 Empty

원칙:

- 사용자를 재촉하지 않는다.
- 한 문장 설명과 한 액션만 둔다.
- 온보딩에서 본 앱 UI preview와 같은 시각 언어를 유지한다.

예시 톤:

- `아직 오늘 기록이 없어요`
- `짧게 남기고 나중에 요약해 볼 수 있어요`
- `일기 쓰기`

### 11.3 Error

원칙:

- 원인, 영향, 다음 행동을 짧게 제공한다.
- `문제가 발생했습니다`만 단독으로 쓰지 않는다.
- 원문, 백업 비밀번호, 파일 경로 같은 민감 정보는 표시하지 않는다.

예시 톤:

- `일기를 불러오지 못했어요`
- `로컬 저장소를 다시 확인해 주세요`
- `다시 시도`

### 11.4 Unsupported

온디바이스 AI 미지원 상태:

- 자동 클라우드 fallback이 없다는 점을 명확히 한다.
- 사용자가 책임을 느끼는 문구를 피한다.
- 지원 기기 확장 가능성은 과장하지 않는다.

## 12. Onboarding

앱 가이드는 실제 앱 UI preview 중심으로 구성한다.

권장 구성:

1. 일기 목록과 빠른 작성 preview
2. 요약 결과 preview
3. Google Drive 암호화 백업 preview
4. 생체 인증과 설정 preview

원칙:

- 각 화면은 실제 1차 출시 UI와 같은 토큰을 사용한다.
- 설명 문장은 짧게 쓴다.
- `건너뛰기`는 작게 제공한다.
- 개인정보/민감정보 가능성 고지, AI 처리 설명, 백업 opt-in은 앱 가이드와 분리한다.
- 앱 가이드 이미지는 법적 고지처럼 보이지 않게 한다.

## 13. Privacy, AI, Backup Screens

개인정보, AI, 백업 화면은 신뢰가 핵심이다.

표현 원칙:

- 기능 홍보 문구보다 처리 위치, 선택 여부, 한계를 먼저 말한다.
- `절대`, `완벽`, `항상 안전` 같은 보증 표현을 피한다.
- 사용자가 켜기 전에는 백업이 동작하지 않는다는 점을 명확히 한다.
- 온디바이스 AI는 지원 기기와 OS 조건에 따라 다를 수 있음을 표현한다.
- Google Drive 연결 권한 요청은 화면상 이유와 범위를 먼저 설명한다.

UI 원칙:

- 고지는 별도 화면 또는 명확한 section으로 제공한다.
- 긴 설명은 progressive disclosure를 사용하되 핵심 고지는 접어두지 않는다.
- 법적/위험 액션은 버튼 색, 문구, 확인 흐름을 일반 기능과 분리한다.

## 14. Haptic Feedback

햅틱은 의미 있는 결과에만 적용한다.

적용:

- 일기 저장 성공: light success
- 일기 삭제 확정: warning
- 백업 완료: success
- 백업 실패: error
- 복구 완료: success
- 복구 실패: error

비적용:

- 탭 전환
- 일반 버튼 탭
- 스크롤
- 텍스트 입력
- 앱 가이드 페이지 이동

원칙:

- OS 시스템 햅틱/접근성 설정을 존중한다.
- 생체 인증 성공/실패는 OS 기본 피드백을 우선한다.
- 햅틱 설정이 꺼져 있으면 앱 자체 햅틱을 실행하지 않는다.

## 15. Accessibility

접근성은 1차 출시 기준에 포함한다.

필수 기준:

- 텍스트 대비는 WCAG AA 이상을 목표로 한다.
- 주요 터치 타깃은 44pt 이상으로 유지한다.
- Dynamic Type 또는 font scale을 고려한다.
- 중요한 상태는 색상만으로 전달하지 않는다.
- 삭제, 복구, 연결 해제는 스크린리더에서 결과를 명확히 읽을 수 있어야 한다.
- 일기 원문은 스크린리더 접근성을 막지 않는다. 다만 잠금 상태에서는 OS 보안 정책과 앱 잠금 정책을 따른다.

## 16. Platform Adaptation

Android와 iOS는 같은 정보 구조와 토큰을 공유하되 플랫폼 관습을 따른다.

Android Compose:

- Material 3 컴포넌트를 기반으로 하되 색상, shape, typography는 SumDiary token을 적용한다.
- bottom navigation, modal bottom sheet, snackbar는 Material 관습을 따른다.
- Back handling이 있는 위험 흐름은 명시적으로 확인한다.

iOS SwiftUI:

- NavigationStack, sheet, confirmationDialog 등 iOS 관습을 따른다.
- SF Symbols를 사용하되 은유는 Android와 맞춘다.
- Face ID/Touch ID 표현은 Apple 플랫폼 용어를 사용한다.

공통:

- 기능명, 상태명, 고지 문구의 의미는 동일하게 유지한다.
- 플랫폼별 UI 차이가 법적 의미 차이로 보이면 안 된다.

## 17. Copy Rules

톤:

- 짧고 미니멀하게
- 과장 없이
- 사용자를 재촉하지 않게
- 기술 한계를 숨기지 않게

권장:

- `기기 안에서 요약해요`
- `백업은 사용자가 켠 뒤에만 동작해요`
- `Google Drive에 암호화된 백업을 저장해요`
- `이 작업은 되돌리기 어려워요`

금지:

- `완벽하게 안전해요`
- `절대 유출되지 않아요`
- `AI가 당신의 하루를 완벽히 이해해요`
- `지금 바로 시작하지 않으면 놓쳐요`

## 18. Implementation Checklist

UI 구현 전 확인:

- 라이트/다크 토큰이 플랫폼별 theme에 반영되었는가
- typography scale이 Android/iOS에서 같은 위계를 가지는가
- 일기, 요약, 설정 탭이 같은 navigation 구조를 가지는가
- 온보딩 preview가 실제 UI 토큰과 일치하는가
- 개인정보/AI/백업 고지가 앱 가이드와 분리되었는가
- 삭제/복구/연결 해제 화면이 danger 정책을 따르는가
- loading/empty/error/unsupported 상태가 각 주요 화면에 있는가
- 햅틱이 의미 있는 결과에만 적용되었는가
- 스크린리더와 font scale에서 핵심 흐름이 막히지 않는가
