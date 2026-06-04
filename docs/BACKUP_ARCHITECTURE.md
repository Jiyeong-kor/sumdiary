# SumDiary Backup Architecture

## 1. 문서 정보

- 제품명: SumDiary
- 문서 목적: 외부 클라우드 연결형 백업/동기화 구조, 암호화 방식, 복구/삭제 정책, 스토어 심사 대응 기준을 정의한다.
- 작성일: 2026-06-04
- 상태: 초안
- 1차 출시 국가: 대한민국
- 1차 출시 플랫폼: Android, iOS

## 2. 결정 요약

- SumDiary 자체 계정은 제공하지 않는다.
- SumDiary 자체 백업 서버는 운영하지 않는다.
- 사용자가 직접 연결한 외부 클라우드에 암호화된 백업 파일만 저장한다.
- 1차 출시 백업 제공자는 Google Drive 단독으로 확정한다.
- Dropbox와 iCloud Drive는 후속 제공자 후보로 둔다.
- 일기 원문, 요약문, 감정 태그는 클라우드 업로드 전 앱 안에서 암호화한다.
- 외부 클라우드에는 평문 일기 데이터가 저장되지 않아야 한다.
- 클라우드 백업은 기본 꺼짐이며, 사용자가 명시적으로 켜야 한다.
- 온디바이스 AI 요약은 외부 AI 서버로 원문을 전송하지 않는다.

## 3. 목표

- Android와 iOS 간 동일한 백업/복구 경험을 제공한다.
- 사용자가 자기 클라우드 계정에 데이터를 직접 보관하도록 한다.
- SumDiary가 서버 저장소를 운영하지 않아 침해 사고와 운영 책임을 줄인다.
- 백업 파일이 외부 클라우드에 노출되더라도 원문 내용을 읽을 수 없도록 한다.
- 스토어 심사에서 백업, 개인정보, OAuth 권한, 데이터 삭제 범위를 명확히 설명할 수 있게 한다.

## 4. 비목표

- 1차 출시에서 SumDiary 자체 로그인 서버를 만들지 않는다.
- 1차 출시에서 SumDiary 자체 클라우드 저장소를 운영하지 않는다.
- 1차 출시에서 클라우드 AI 요약 fallback을 제공하지 않는다.
- 1차 출시에서 여러 사용자의 공유 백업, 가족 공유, 협업 기능을 제공하지 않는다.
- 1차 출시에서 백업 파일 평문 열람 기능을 제공하지 않는다.

## 5. 전체 구조

```text
Local Database
  -> Backup Snapshot Builder
  -> Backup Serializer
  -> Backup Encryptor
  -> Cloud Provider Adapter
  -> User-owned Cloud Storage
```

복구 흐름은 반대 방향으로 동작한다.

```text
User-owned Cloud Storage
  -> Cloud Provider Adapter
  -> Backup Downloader
  -> Backup Decryptor
  -> Backup Parser
  -> Conflict Resolver
  -> Local Database
```

## 6. 모듈 구조 제안

### shared:domain-backup

- 백업 use case interface를 정의한다.
- provider별 구현에 의존하지 않는다.
- 암호화된 백업 파일의 도메인 모델을 정의한다.

### shared:data-backup

- 백업 snapshot 생성과 복구 merge 로직을 구현한다.
- serializer/deserializer를 포함한다.
- 암호화 interface를 호출하되 플랫폼 보안 API 직접 의존은 피한다.

### shared:feature-backup

- 백업 설정, 연결 상태, 백업 실행 상태, 복구 상태를 관리한다.
- Android/iOS UI에서 공통 상태 모델을 사용할 수 있게 한다.

### app:android

- Google Drive OAuth 연결 UI를 제공한다.
- Android Keystore 기반 키 보관 또는 키 파생 보조 기능을 제공한다.
- Android용 provider adapter를 구현한다.

### app:ios

- Google Drive OAuth 연결 UI를 제공한다.
- iOS Keychain 기반 키 보관 또는 키 파생 보조 기능을 제공한다.
- iOS용 provider adapter를 구현한다.
- iOS GoogleSignIn provider는 `drive.appdata` scope를 요청하고, SwiftUI `onOpenURL` callback에서 GoogleSignIn redirect URL을 처리한다.

## 7. Provider 전략

### 7.1 1차 provider: Google Drive

Google Drive를 1차 출시 provider로 확정한다.

선정 이유:

- Android와 iOS 모두에서 사용 가능하다.
- 사용자가 이미 계정을 보유할 가능성이 높다.
- 크로스 플랫폼 복구에 적합하다.
- OAuth와 API 문서가 안정적이다.

요구사항:

- 최소 권한 scope를 사용한다.
- 가능하면 앱 전용 폴더 또는 앱이 생성한 파일 범위만 접근한다.
- 사용자가 연결 해제할 수 있어야 한다.
- 사용자가 SumDiary 백업 파일 삭제를 앱 안에서 시작할 수 있어야 한다.
- OAuth 검증이 필요한 경우 출시 일정에 검증 기간을 반영한다.

### 7.2 후속 후보: Dropbox

Dropbox는 2차 provider 후보로 둔다.

요구사항:

- Dropbox App Console 정책을 검토한다.
- 앱 전용 폴더 권한을 우선 검토한다.
- Dropbox OAuth 연결, 연결 해제, 백업 파일 삭제를 동일 UX로 제공한다.

### 7.3 후속 후보: iCloud Drive

iCloud Drive는 iOS 사용자 경험에는 좋지만 Android와 동기화할 수 없으므로 1차 공통 백업 provider로 쓰지 않는다.

용도:

- iOS 전용 로컬 백업 옵션
- iOS 사용자의 보조 백업 경로

제약:

- Android와 cross-platform restore가 어렵다.
- App Store Privacy 답변과 iCloud 접근 안내가 필요하다.

## 8. 백업 데이터 범위

### 포함

- DiaryEntry
  - id
  - date
  - time
  - content
  - createdAt
  - updatedAt
- Summary
  - id
  - type
  - periodStart
  - periodEnd
  - text
  - emotions
  - sourceEntryIds
  - createdAt
  - updatedAt
- 사용자 설정
  - locale
  - weekStartDay
  - backupEnabled
  - aiSummaryEnabled
  - emotionTagsVisible
- 백업 메타데이터
  - backupVersion
  - appVersion
  - platform
  - deviceIdHash
  - createdAt

### 제외

- OAuth access token
- OAuth refresh token
- 암호화 키 원문
- crash log
- analytics event
- debug log
- 온디바이스 AI 내부 prompt 로그
- OS 계정 정보 원문

## 9. 백업 파일 포맷

백업 파일은 단일 암호화 컨테이너로 저장한다.

파일명:

```text
sumdiary-backup-v1.sdbak
```

권장 컨테이너 구조:

```json
{
  "format": "sumdiary.backup",
  "version": 1,
  "createdAt": "2026-06-04T00:00:00Z",
  "encryption": {
    "algorithm": "AES-256-GCM",
    "kdf": "Argon2id or PBKDF2",
    "salt": "base64",
    "nonce": "base64"
  },
  "payload": "base64 encrypted bytes"
}
```

암호화된 payload 내부 구조:

```json
{
  "metadata": {
    "backupVersion": 1,
    "appVersion": "1.0.0",
    "createdAt": "2026-06-04T00:00:00Z"
  },
  "entries": [],
  "summaries": [],
  "settings": {}
}
```

주의:

- 외부 클라우드에는 payload 평문을 저장하지 않는다.
- 백업 파일 metadata에도 일기 내용이나 감정 태그를 넣지 않는다.
- 파일명에도 사용자 이름, 날짜별 감정, 제목 등을 넣지 않는다.

## 10. 암호화 정책

### 10.1 기본 원칙

- 백업 파일은 앱 안에서 암호화한 뒤 업로드한다.
- 외부 클라우드 제공자는 평문 내용을 볼 수 없어야 한다.
- SumDiary는 자체 서버가 없으므로 서버 관리 키를 사용하지 않는다.
- 1차 출시 암호화 정책은 사용자 키 기반 E2EE로 확정한다.
- Android 백업 암호화 구현은 PBKDF2-HMAC-SHA256으로 파생한 256-bit 키와 AES-GCM을 사용한다.
- 백업 payload는 JSON snapshot을 bytes로 직렬화한 뒤 암호화하며, 외부 클라우드에는 Base64 암호문만 저장한다.
- 개발용 fake 암호화 구현은 출시 경로에서 사용하지 않는다.

### 10.2 키 방식

권장 방식:

- 사용자가 백업 비밀번호 또는 복구 문구를 만든다.
- 앱은 비밀번호에서 암호화 키를 파생한다.
- 파생 키로 백업 payload를 암호화한다.
- 기기에는 원문 비밀번호를 저장하지 않는다.

추가 편의 옵션:

- 사용자가 동의하면 OS 보안 저장소에 복구 보조 키를 저장한다.
- Android는 Keystore를 사용한다.
- iOS는 Keychain을 사용한다.
- Face ID, Touch ID, Android 생체 인증은 1차 출시에서 백업 열기와 앱 잠금 해제의 편의 UX로 제공한다.
- 생체 인증은 OS 보안 저장소에 보관된 키 접근을 보호하는 수단으로만 사용하고, 암호화 키 자체를 대체하지 않는다.
- 생체 인증 정보는 SumDiary가 직접 수집하거나 저장하지 않는다.

### 10.3 복구 UX

- 새 기기에서 복구하려면 외부 클라우드 연결과 백업 비밀번호 또는 복구 문구가 필요하다.
- 기존 기기에서는 사용자가 동의한 경우 Face ID, Touch ID, Android 생체 인증으로 백업 키 접근을 승인할 수 있다.
- 새 기기, OS 재설치, Keychain/Keystore 초기화, 생체 인증 재등록 이후에는 생체 인증만으로 복구할 수 없다.
- 사용자가 비밀번호/복구 문구를 잃어버리면 SumDiary는 백업을 복구할 수 없다.
- 이 제한은 백업 설정 단계에서 명확히 고지한다.

권장 문구:

```text
이 백업은 사용자의 기기에서 암호화됩니다. 비밀번호를 잃어버리면 SumDiary도 백업을 복구할 수 없습니다.
```

## 11. 동기화 정책

### 11.1 기본 방식

1차 출시는 파일 기반 snapshot 동기화를 사용한다.

- 앱이 로컬 데이터를 snapshot으로 묶는다.
- snapshot을 암호화한다.
- 외부 클라우드의 기존 백업 파일을 갱신한다.
- 다른 기기에서 같은 파일을 다운로드해 복구/merge한다.

### 11.2 충돌 해결

1차 기본 정책:

- 같은 `DiaryEntry.id`가 충돌하면 `updatedAt`이 최신인 항목을 우선한다.
- 서로 다른 ID의 일기는 모두 보존한다.
- 요약은 동일 기간과 동일 type 기준으로 최신 `updatedAt`을 우선한다.
- 복구 전 사용자에게 "백업 데이터 병합"과 "로컬 데이터 교체" 선택지를 제공한다.

권장 기본값:

- 병합을 기본값으로 둔다.
- 전체 교체는 고급 옵션으로 둔다.

### 11.3 자동 동기화

1차 출시에서는 과도한 자동 동기화를 피한다.

- 앱 시작 시 1회 상태 확인
- 사용자가 일기 저장 후 일정 시간 debounce 후 백업
- 사용자가 수동 백업 실행 가능
- Wi-Fi 전용 백업 옵션 제공 검토

## 12. 삭제 정책

### 12.1 로컬 데이터 삭제

사용자는 앱 안에서 다음 데이터를 삭제할 수 있어야 한다.

- 일기 원문
- 요약문
- 감정 태그
- 로컬 색인
- 로컬 캐시
- 백업 설정
- 외부 클라우드 연결 토큰

### 12.2 외부 클라우드 백업 파일 삭제

사용자는 앱 안에서 외부 클라우드의 SumDiary 백업 파일 삭제를 시작할 수 있어야 한다.

삭제 범위:

- `sumdiary-backup-v1.sdbak`
- provider별 앱 전용 폴더 안의 SumDiary 백업 파일
- 향후 분할 백업이 생기면 모든 part 파일

삭제 후 앱은 다음 상태를 표시한다.

- 삭제 성공
- 연결 만료로 삭제 실패
- 권한 부족으로 삭제 실패
- 파일을 찾을 수 없음
- 네트워크 오류

### 12.3 외부 클라우드 계정 삭제

SumDiary는 Google/Dropbox/Apple 계정을 삭제할 수 없다.

앱은 다음을 안내한다.

- SumDiary 앱의 외부 클라우드 연결 해제
- SumDiary 백업 파일 삭제
- 외부 클라우드 계정 자체 삭제는 해당 제공자 계정 설정에서 진행

## 13. OAuth 및 권한 정책

### 13.1 최소 권한

- 앱 기능에 필요한 최소 scope만 요청한다.
- 전체 Drive 접근 권한은 피한다.
- 앱 전용 폴더 또는 앱이 생성한 파일 범위 접근을 우선한다.
- 1차 Google Drive 백업은 앱 전용 데이터 폴더 접근 scope인 `https://www.googleapis.com/auth/drive.appdata`를 우선 사용한다.
- 사용자 Drive 전체 파일을 읽거나 탐색하는 scope는 1차 출시 범위에서 제외한다.
- 권한 요청 화면에서 왜 필요한지 앱 안에서 먼저 설명한다.

### 13.2 토큰 보관

- OAuth token은 OS 보안 저장소에 저장한다.
- Android는 Keystore/EncryptedSharedPreferences 사용을 검토한다.
- iOS는 GoogleSignIn SDK의 저장 상태와 Keychain 정책을 검토한다.
- 토큰은 백업 파일에 포함하지 않는다.
- 연결 해제 시 로컬 토큰을 삭제한다.

### 13.3 검증 증적

출시 전 다음 증적을 준비한다.

- 요청 scope 목록
- scope별 사용 목적
- OAuth consent screen 캡처
- Google OAuth 검증 필요 여부
- API 호출 네트워크 캡처
- 백업 파일이 암호화되어 있음을 보여주는 테스트 결과

## 14. 사용자 화면 요구사항

### 백업 설정

- 백업 상태
- 연결된 provider
- 마지막 백업 시간
- 마지막 복구 시간
- 자동 백업 on/off
- 수동 백업
- 복구
- 백업 파일 삭제
- 클라우드 연결 해제

### 백업 켜기

1. 백업 설명 표시
2. 암호화와 복구 불가 고지
3. provider 선택
4. OAuth 연결
5. 백업 비밀번호 또는 복구 문구 생성
6. 첫 백업 실행
7. 성공 상태 표시

### 복구

1. provider 선택
2. OAuth 연결
3. 백업 파일 탐색
4. 백업 비밀번호 또는 복구 문구 입력
5. 복호화 검증
6. 병합/교체 선택
7. 복구 완료

## 15. 실패 상태

- 외부 클라우드 연결 취소
- OAuth 인증 만료
- OAuth scope 부족
- 네트워크 끊김
- 저장 용량 부족
- 백업 파일 없음
- 백업 파일 손상
- 암호 불일치
- 지원하지 않는 백업 버전
- 충돌 해결 실패
- 로컬 DB 저장 실패

모든 실패 상태는 사용자가 다음 행동을 이해할 수 있게 표시한다.

## 16. 스토어 심사 대응

### Google Play

- Data safety form에 외부 클라우드 연결과 백업 데이터 처리를 정확히 반영한다.
- Google Drive API scope는 최소 권한으로 제한한다.
- OAuth 검증이 필요한 경우 개인정보처리방침, 데모 영상, 테스트 계정 또는 테스트 절차를 준비한다.
- 백업은 사용자가 명시적으로 켠 경우에만 수행된다고 설명한다.

### Apple App Store

- App Privacy 답변에 외부 클라우드 연결, 백업 데이터 처리, OAuth SDK 사용 여부를 반영한다.
- iCloud Drive를 지원할 경우 Apple 계정 및 iCloud 접근 제어가 사용자 설정에 종속됨을 설명한다.
- 리뷰어 노트에 외부 클라우드 미연결 상태에서도 핵심 기능이 동작함을 설명한다.

## 17. 보안 테스트 체크리스트

- 백업 파일을 열어도 일기 원문이 보이지 않는다.
- 백업 파일 metadata에 민감정보가 없다.
- OAuth token이 백업 파일에 포함되지 않는다.
- 클라우드 연결 해제 시 로컬 token이 삭제된다.
- 백업 파일 삭제 후 provider에서 파일이 보이지 않는다.
- 잘못된 비밀번호로 복구할 수 없다.
- 앱 로그에 일기 원문, 요약문, 감정 태그가 남지 않는다.
- 네트워크 캡처에 평문 일기 데이터가 보이지 않는다.

## 18. 오픈 이슈

- 백업 비밀번호를 기본 복구 수단으로 확정하고, 복구 문구는 고급 옵션 또는 후속 기능으로 둘지 결정한다.
- 자동 백업 주기와 Wi-Fi 전용 옵션을 결정한다.
- 백업 파일 버전 migration 정책을 상세화한다.
