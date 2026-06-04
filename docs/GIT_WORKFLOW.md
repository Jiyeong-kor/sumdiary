# SumDiary Git Workflow

## 1. 원칙

- 모든 작업은 GitHub issue에서 시작한다.
- 브랜치는 issue 번호를 포함한다.
- PR은 관련 issue를 연결한다.
- 커밋은 논리적 단위로 나눈다.
- 커밋 메시지는 Conventional Commits 형식을 따르되 본문은 한국어로 작성한다.
- 관련 issue는 커밋 본문과 PR 본문에 `Refs #번호`로 연결한다.
- issue를 자동으로 닫아야 할 때만 `Closes #번호`를 사용한다.

## 2. 기본 브랜치

- 기본 브랜치: `master`
- 직접 작업 금지: `master`
- 모든 기능/수정/문서 작업은 별도 브랜치에서 진행한다.

## 3. Issue 규칙

작업 전 issue를 먼저 만든다.
issue 생성 직후 작업 성격과 영향 영역에 맞는 label을 반드시 붙인다.

Issue 제목 형식:

```text
[type] 작업 요약
```

예시:

```text
[docs] 제품 요구사항과 백업 설계 문서 추가
[feat] Google Drive 백업 도메인 인터페이스 추가
[fix] 빈 일기 요약 상태 처리
```

Issue 본문에는 다음을 포함한다.

- 배경
- 작업 범위
- 완료 조건
- 관련 문서 또는 화면
- 법무/개인정보 영향 여부

Issue label은 최소 1개의 `type:*` label과, 해당되는 경우 1개 이상의 `area:*` 또는 `process:*` label을 붙인다.

예시:

```text
[docs] 제품 요구사항과 개발 프로세스 문서 추가
labels: type:docs, area:product, area:privacy, area:backup
```

```text
[ci] Android/KMP 기본 CI 워크플로 추가
labels: type:ci, area:android, area:build
```

```text
[feature] 일기 목록 상태 연동
labels: type:feature, area:entry, area:android
```

## 4. 브랜치 규칙

브랜치 이름:

```text
type/issue-number-short-title
```

허용 type:

- `feature`
- `fix`
- `docs`
- `chore`
- `refactor`
- `test`
- `build`
- `ci`

예시:

```text
docs/1-product-requirements
feature/12-google-drive-backup
fix/18-summary-empty-state
refactor/24-backup-module-boundary
```

## 5. 커밋 규칙

커밋 메시지 형식:

```text
type(scope): 한국어 요약

Refs #이슈번호
```

예시:

```text
docs(product): 제품 요구사항과 백업 설계 문서 추가

Refs #1
```

```text
feat(backup): 백업 도메인 인터페이스 추가

Refs #12
```

```text
fix(summary): 빈 일기 요약 상태 처리

Refs #18
```

허용 type:

- `feat`: 사용자 기능 추가
- `fix`: 버그 수정
- `docs`: 문서 변경
- `chore`: 설정, 정리, 관리 작업
- `refactor`: 동작 변경 없는 구조 개선
- `test`: 테스트 추가/수정
- `build`: 빌드 설정 변경
- `ci`: CI 설정 변경

권장 scope:

- `product`
- `privacy`
- `backup`
- `diary`
- `summary`
- `android`
- `ios`
- `build`
- `ci`

## 6. PR 규칙

PR은 가능한 한 draft로 먼저 연다.
PR 생성 직후 관련 issue와 동일하거나 더 구체적인 label을 반드시 붙인다.

PR 제목 형식:

```text
type(scope): 한국어 요약
```

PR 본문에는 다음을 포함한다.

```text
## 작업 내용

- 변경 사항 요약

## 확인 사항

- [ ] 빌드 확인
- [ ] 테스트 확인
- [ ] 개인정보/스토어 심사 영향 검토

## 관련 이슈

Refs #이슈번호
```

issue를 PR merge 시 자동으로 닫아야 한다면 `Refs` 대신 `Closes`를 사용한다.

PR label은 다음 원칙으로 붙인다.

- 관련 issue의 label을 기본으로 승계한다.
- PR 안에 추가 성격이 있으면 `type:*` label을 추가한다.
- 빌드, CI, Android, 백업, 개인정보 등 영향 영역이 있으면 `area:*` label을 추가한다.
- GitHub 운영 규칙 변경은 `process:github`을 붙인다.

예시:

```text
docs(product): 제품 요구사항과 개발 프로세스 문서 추가
labels: type:docs, type:ci, type:fix, area:product, area:privacy, area:backup, area:android, area:build
```

```text
feat(entry): 일기 목록 상태를 화면에 연동
labels: type:feature, type:chore, area:entry, area:android, area:build
```

## 7. Label 규칙

현재 프로젝트에서 사용하는 label 체계:

```text
type:docs      문서 변경
type:ci        CI 워크플로 변경
type:fix       버그 또는 빌드 실패 수정
type:feature   사용자 기능 추가 또는 개선
type:chore     설정, 운영, 정리 작업

area:product   제품 요구사항과 범위
area:privacy   개인정보, 보안, 법무 영향
area:backup    백업, 복구, 외부 클라우드 연동
area:android   Android 앱과 Android 빌드 설정
area:build     Gradle, KMP, 의존성, 빌드 안정성
area:entry     일기 작성, 일기 목록, entry feature

process:github GitHub issue, PR, label, workflow 운영
```

새 issue나 PR을 만들 때 label이 부족하면 먼저 label을 생성한 뒤 붙인다.
label 생성/부착은 GitHub 작업 완료 조건에 포함한다.

## 8. Merge 규칙

- PR 리뷰 또는 자체 점검 후 merge한다.
- 문서만 변경한 PR도 관련 issue를 남긴다.
- 개인정보, 백업, AI, 스토어 심사와 관련된 PR은 문서 업데이트 여부를 확인한다.
- UI 구현 PR은 앱 IA, UX flow, 디자인 시스템 문서와 연결되어야 한다.
- 기능 PR은 CI 통과 후 merge한다.
- `master`에 merge된 후 필요하면 issue를 닫는다.

## 9. 현재 문서 작업 커밋 예시

현재 제품 문서 작업을 커밋한다면 다음 형식을 사용한다.

```text
docs(product): 제품 요구사항과 백업 설계 문서 추가

Refs #이슈번호
```
