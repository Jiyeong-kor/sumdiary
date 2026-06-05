# 요약 AI 개인정보 감사

## 목적

SumDiary의 일기 원문은 요약 기능을 위해 외부 AI 서버로 전송하지 않는다. 요약 모듈은 플랫폼 온디바이스 provider 경계만 사용하고, 외부 네트워크 호출은 백업 기능의 Google Drive API 경계에만 둔다.

## 현재 구현 경계

- Android 요약은 `AndroidMlKitSummarizerEngine`에서 ML Kit GenAI Summarization client를 통해 실행한다.
- iOS 요약은 `IosFoundationModelsSummarizerEngine`에서 iOS native `FoundationModelsSummarizerProvider`로 위임한다.
- `shared:data-summary`는 Ktor, OkHttp, URLSession, `java.net.URL`, 외부 URL 문자열을 사용하지 않는다.
- Google Drive 백업은 `shared:data-backup`의 별도 경계이며, 암호화된 백업 파일 업로드/다운로드를 위해 네트워크를 사용한다.

## 자동 검증

다음 테스트가 `shared:data-summary`의 common/android/iOS main source set에서 네트워크 클라이언트와 외부 URL 사용을 검사한다.

- `:shared:data-summary:testDebugUnitTest`
- `SummaryNoExternalNetworkGuardTest`

검사 대상 금지 토큰:

- `io.ktor.client`
- `okhttp3`
- `HttpClient`
- `URLSession`
- `NSURLSession`
- `openConnection`
- `java.net.URL`
- `https://`
- `http://`

## 남은 수동 검증

- Android ML Kit GenAI 지원 기기와 미지원 기기에서 실제 요약 UX 확인
- iOS Foundation Models 지원 기기와 미지원 기기에서 실제 요약 UX 확인
- 로그와 crash report에 일기 원문 또는 요약문이 남지 않는지 실기기 확인
