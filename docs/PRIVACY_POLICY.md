# SumDiary 개인정보처리방침

## 문서 상태

- 상태: 출시 후보 초안
- 기준일: 2026-06-04
- 적용 대상: SumDiary Android 및 iOS 앱
- 출시 전 필요: 개인정보 보호 책임자 또는 법률 전문가 검토, 배포 URL 확정

이 문서는 SumDiary의 현재 제품 설계와 코드 기준 데이터 흐름을 설명한다. 법률 자문을 대체하지 않으며, 스토어 제출 전 실제 구현, 제3자 SDK, Google Drive OAuth 설정, 배포 지역에 맞춰 최종 검토해야 한다.

## 1. 처리하는 개인정보

SumDiary는 개인 일기 앱으로, 사용자가 직접 입력한 일기와 앱 사용에 필요한 설정을 기기 안에 저장한다.

| 항목 | 예시 | 처리 목적 | 기본 저장 위치 |
| --- | --- | --- | --- |
| 일기 본문 | 사용자가 작성한 텍스트 | 일기 작성, 수정, 삭제, 요약 | 사용자 기기 |
| 일기 작성일 | 날짜와 시간 | 일기 목록과 요약 기간 계산 | 사용자 기기 |
| 요약 결과 | 일간/주간 요약문 | 사용자가 작성한 일기 돌아보기 | 사용자 기기 |
| 앱 설정 | 첫 실행 안내 완료 여부, 백업 설정 상태 | 앱 사용 흐름 유지 | 사용자 기기 |
| 백업 비밀번호 | 사용자가 입력한 백업 암호 | 백업 파일 암호화와 복구 | 백업 파일에 저장하지 않음 |
| Google Drive OAuth token | Google Drive 연결 권한 | 사용자가 선택한 백업 파일 업로드/삭제 | OS 보안 저장소에 저장해야 함 |

## 2. 일기와 요약 데이터 처리

- 일기 원문은 요약 목적으로 외부 AI 서버로 전송하지 않는 것을 원칙으로 한다.
- 온디바이스 요약 엔진이 지원되지 않는 기기에서는 자동 클라우드 AI fallback을 실행하지 않는다.
- 현재 코드의 기본 요약 엔진은 미지원 상태를 반환하며, 실제 온디바이스 AI SDK 연동 전에는 출시 준비 완료로 보지 않는다.

## 3. 백업 데이터 처리

- Google Drive 백업은 사용자가 직접 연결하고 실행한 경우에만 동작해야 한다.
- 백업 파일은 앱에서 암호화한 뒤 외부 클라우드에 저장해야 한다.
- 백업 파일에는 OAuth access token, refresh token, 백업 비밀번호, 암호화 키 원문을 포함하지 않는다.
- 사용자가 백업 비밀번호를 잃어버리면 SumDiary도 백업 파일을 복구할 수 없다.
- 현재 Android 앱 연결 경로는 Google Identity Services 기반 OAuth access token provider와 Google Drive API client를 사용한다. iOS 앱은 GoogleSignIn 기반 Google Drive OAuth provider와 URL callback 경로를 포함한다. 제출 전 실제 OAuth client id 설정, iOS archive 검증, scope 검토를 완료해야 한다.

## 4. 제3자 서비스

### Google Drive

1차 출시 백업 제공자는 Google Drive로 계획한다.

- 사용 목적: 사용자가 선택한 암호화 백업 파일 저장과 삭제
- 우선 검토 scope: `https://www.googleapis.com/auth/drive.appdata`
- 출시 전 필요: OAuth consent screen, scope 검토표, 검증 필요 여부 확인, 사용자에게 연결 해제와 백업 삭제 방법 안내
- Android 빌드 설정: 제출 전 `sumdiary.googleDriveOAuthClientId` Gradle property를 실제 OAuth client id로 설정해야 한다.
- iOS 빌드 설정: 제출 전 Xcode build setting의 `GOOGLE_DRIVE_IOS_CLIENT_ID`와 `GOOGLE_DRIVE_IOS_REVERSED_CLIENT_ID`를 실제 iOS OAuth client 값으로 설정해야 한다.
- 구현 상태: Android는 Google Identity Services 기반 OAuth access token provider와 Google Drive `appDataFolder` 업로드, 다운로드, 삭제 API client를 코드에 포함한다. iOS는 GoogleSignIn 기반 provider와 callback hook을 포함하지만, macOS/Xcode archive 검증과 실제 OAuth client 설정은 남아 있다.

### 분석 및 오류 수집 SDK

현재 문서 기준으로 별도 분석 SDK 또는 crash reporting SDK 도입은 확정하지 않았다. 도입 시 수집 항목, 공유 여부, 보관 기간, App Privacy와 Data safety 답변을 갱신해야 한다.

## 5. 보관 및 삭제

- 로컬 일기와 요약은 사용자가 앱에서 삭제하거나 앱 데이터를 삭제할 때 제거된다.
- 외부 백업 파일은 사용자가 백업 삭제 기능을 실행해야 Google Drive에서 삭제된다.
- Google Drive OAuth 권한 해제는 앱 내 연결 해제와 Google 계정 권한 관리 양쪽 절차를 안내해야 한다.

## 6. 국외 이전

Google Drive 백업을 사용하면 암호화된 백업 파일이 Google의 인프라에 저장될 수 있다. 출시 전 실제 OAuth 설정, Google 약관, 배포 국가에 맞춰 국외 이전 고지를 검토한다.

## 7. 아동 개인정보

SumDiary의 1차 출시 대상은 일반 사용자이며, 아동을 주 대상으로 설계하지 않는다. 스토어 콘텐츠 등급과 연령 제한 답변은 출시 전 별도로 확정한다.

## 8. 이용자 권리

사용자는 앱 안에서 일기와 요약 데이터를 삭제할 수 있어야 한다. 외부 백업을 사용한 경우에는 백업 파일 삭제와 Google 계정 권한 해제 방법을 함께 안내해야 한다.

## 9. 출시 전 확정 필요 항목

- 실제 Google Drive OAuth client와 scope
- Google OAuth 검증 필요 여부
- 개인정보처리방침 배포 URL
- 문의 이메일 또는 운영자 연락처
- App Store App Privacy 답변
- Google Play Data safety 답변
- 실제 온디바이스 AI SDK와 제3자 SDK 목록
