# 민감정보 로그 감사

## 목적

SumDiary는 일기 원문, 요약문, OAuth token, 백업 비밀번호, 암호화 키를 로그나 crash report breadcrumb에 남기지 않는다. 제품 코드는 공용 logger 초기화 경계 외에 직접 로그 API를 사용하지 않는다.

## 자동 검증

다음 테스트는 Android, iOS, shared 제품 소스의 `src/*Main` 파일을 스캔한다.

- `:shared:core-util:testDebugUnitTest`
- `SensitiveLogOutputGuardTest`

금지 대상:

- `println(`
- `printStackTrace(`
- `android.util.Log`
- `Log.d(`, `Log.i(`, `Log.w(`, `Log.e(`
- `FirebaseCrashlytics`, `Crashlytics`
- `recordException(`, `setCustomKey(`
- `breadcrumb`
- `Napier.`

허용 경계:

- `shared/core-util/src/commonMain/kotlin/com/jeong/sumdiary/core/util/NapierLogger.kt`

## 남은 수동 검증

- Android 실기기 logcat에서 일기 원문, 요약문, OAuth token, 백업 비밀번호, 암호화 키가 남지 않는지 확인
- iOS device console에서 동일 민감정보가 남지 않는지 확인
- crash report 도구를 붙이는 경우 수집 payload에서 동일 민감정보가 제외되는지 확인
