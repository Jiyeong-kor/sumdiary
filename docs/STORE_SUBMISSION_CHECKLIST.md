# SumDiary 스토어 제출 체크리스트

## 문서 상태

- 상태: 출시 후보 체크리스트
- 기준일: 2026-06-05
- 적용 대상: Google Play 및 Apple App Store 제출 전 최종 점검

## 1. 빌드 게이트

- [x] Android debug build warning-free 통과
- [x] Android release build warning-free 통과
- [x] Android release bundle build warning-free 통과
- [ ] Android release signing Gradle property 4종 설정
- [x] iOS simulator arm64 KMP framework compile warning-free 통과
- [x] 백업 암호화 테스트 통과
- [x] 로컬 요약 엔진 테스트 통과
- [x] shared metadata compile 통과
- [ ] `./gradlew checkReleaseReadiness -Psumdiary.releaseReadiness.strict=true` 통과

## 2. Android 제출 준비

- [ ] release signing 설정
- [ ] AAB 생성과 서명 설정 확인
- [x] target SDK와 compile SDK 확인
- [x] 앱 이름, 아이콘, 버전 코드, 버전 이름 확인
- [x] Android manifest 권한 최소화 확인: Google Drive OAuth/API 통신용 `INTERNET`만 선언
- [x] Google Play Data safety 답변 초안 작성
- [ ] Google Play Data safety 답변 최종 제출값 확인
- [ ] 콘텐츠 등급 설문 작성
- [ ] 개인정보처리방침 URL 등록

## 3. iOS 제출 준비

- [x] Xcode 프로젝트 또는 iOS 앱 shell 구조 확정
- [ ] iOS archive build 통과
- [ ] bundle identifier, version, build number 확정
- [ ] iOS `SumDiary.local.xcconfig` Google Drive OAuth client id, reversed URL scheme, Apple Developer Team ID 설정
- [x] App Privacy 답변 초안 작성
- [ ] App Privacy 답변 최종 제출값 확인
- [ ] 개인정보처리방침 URL 등록
- [x] Face ID 사용 문구 추가

## 4. 개인정보와 보안

- [ ] 일기 원문이 외부 AI 서버로 전송되지 않는지 검증
- [ ] Android ML Kit GenAI 요약 지원 기기와 미지원 기기 UX 검증
- [x] iOS Foundation Models 요약 provider 연결
- [ ] iOS Foundation Models 지원 기기와 미지원 기기 UX 검증
- [ ] iOS Face ID/Touch ID/기기 암호 앱 잠금 실기기 검증
- [ ] 백업 파일에 OAuth token, 백업 비밀번호, 암호화 키 원문이 포함되지 않는지 검증
- [ ] 로그와 crash report에 일기 원문, 요약문, OAuth token, 암호화 키가 남지 않는지 검증
- [ ] Google Drive OAuth scope 최소 권한 검토
- [ ] OAuth consent screen 캡처 보관
- [ ] Google OAuth 검증 필요 여부 확인
- [ ] 백업 삭제와 Google 권한 해제 안내 확인

## 5. 기능 검증

- [ ] 첫 실행 안내와 필수 고지 표시
- [ ] 일기 작성, 수정, 삭제
- [ ] 일간 요약
- [ ] 주간 요약
- [ ] 요약 미지원 기기 UX
- [ ] Google Drive 연결
- [ ] 백업 실행
- [ ] 복구 실행
- [ ] 백업 파일 삭제
- [ ] Android 생체/기기 인증 앱 잠금 실기기 검증

## 6. 출시 차단 항목

현재 코드 기준으로 다음 항목은 출시 제출 전 반드시 제거하거나 실제 구현으로 교체해야 한다.

- Google Drive OAuth client id Gradle property 미설정
- iOS Google Drive OAuth client id/reversed URL scheme/Apple Developer Team ID local xcconfig 미설정
- Android release signing property 미설정
- Android ML Kit GenAI 요약 provider 실기기 검증 미완료
- iOS Foundation Models provider Xcode 26 archive 및 실기기 검증 미완료
- iOS 앱 archive build 검증 미완료
- 개인정보처리방침과 이용약관 법무 검토 미완료
