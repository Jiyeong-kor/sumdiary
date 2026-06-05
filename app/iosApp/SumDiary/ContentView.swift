import LocalAuthentication
import SumDiary
import SwiftUI

struct ContentView: View {
    private let controller: IosSampleController

    @Environment(\.scenePhase) private var scenePhase
    @AppStorage("sumdiary.ios.appLockEnabled") private var appLockEnabled = false
    @State private var entryText = ""
    @State private var summaryText = "오늘의 일기를 작성하고 요약을 불러오세요."
    @State private var isLoadingSummary = false
    @State private var appUnlocked = false
    @State private var appLockMessage: String?

    init(
        summarizerProvider: FoundationModelsSummarizerProvider = FoundationModelsSummarizerProvider()
    ) {
        let summarizerEngine = IosFoundationModelsSummarizerEngine(nativeSummarizer: summarizerProvider)
        let factory = IosAppFactory(summarizerEngine: summarizerEngine)
        controller = IosSampleController(factory: factory)
    }

    var body: some View {
        Group {
            if appLockEnabled && !appUnlocked {
                AppLockGateView(
                    message: appLockMessage,
                    canAuthenticate: canAuthenticateWithLocalPolicy(),
                    onUnlock: authenticateForAppUnlock
                )
            } else {
                content
            }
        }
        .onAppear {
            appUnlocked = !appLockEnabled
        }
        .onChange(of: appLockEnabled) { _, enabled in
            appUnlocked = !enabled
            appLockMessage = nil
        }
        .onChange(of: scenePhase) { _, phase in
            guard appLockEnabled else {
                return
            }

            if phase != .active {
                appUnlocked = false
                appLockMessage = nil
            }
        }
    }

    private var content: some View {
        NavigationStack {
            Form {
                Section("일기") {
                    TextEditor(text: $entryText)
                        .frame(minHeight: 160)

                    Button("저장") {
                        controller.createSampleEntry(text: entryText)
                    }
                    .disabled(entryText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                }

                Section("요약") {
                    Text(summaryText)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    Button("오늘 요약 불러오기") {
                        isLoadingSummary = true
                        summaryText = "요약을 만드는 중이에요."
                        controller.loadTodaySummary { text in
                            Task { @MainActor in
                                summaryText = text
                                isLoadingSummary = false
                            }
                        }
                    }
                    .disabled(isLoadingSummary)
                }

                Section("보안") {
                    Toggle(isOn: appLockBinding) {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Face ID/Touch ID 앱 잠금")
                            Text(appLockDescription)
                                .font(.footnote)
                                .foregroundStyle(.secondary)
                        }
                    }
                }
            }
            .navigationTitle("SumDiary")
        }
    }

    private var appLockBinding: Binding<Bool> {
        Binding(
            get: { appLockEnabled },
            set: { newValue in
                if newValue {
                    authenticateForAppLockEnable()
                } else {
                    appLockEnabled = false
                    appUnlocked = true
                    appLockMessage = "앱 잠금을 껐어요."
                }
            }
        )
    }

    private var appLockDescription: String {
        if appLockEnabled {
            return "켜짐 · 앱을 열 때 기기 인증으로 일기 화면을 보호해요."
        }
        if canAuthenticateWithLocalPolicy() {
            return "꺼짐 · 탭해서 Face ID, Touch ID 또는 기기 암호 인증을 켤 수 있어요."
        }
        return "사용 불가 · 기기 설정에서 Face ID, Touch ID 또는 암호를 먼저 등록해 주세요."
    }

    private func authenticateForAppLockEnable() {
        authenticate(reason: "SumDiary 앱 잠금을 켜려면 기기 인증이 필요해요.") { success, message in
            if success {
                appLockEnabled = true
                appUnlocked = true
                appLockMessage = "앱 잠금을 켰어요."
            } else {
                appLockMessage = message
            }
        }
    }

    private func authenticateForAppUnlock() {
        authenticate(reason: "일기와 요약을 보려면 기기 인증이 필요해요.") { success, message in
            if success {
                appUnlocked = true
                appLockMessage = nil
            } else {
                appLockMessage = message
            }
        }
    }

    private func canAuthenticateWithLocalPolicy() -> Bool {
        let context = LAContext()
        return context.canEvaluatePolicy(.deviceOwnerAuthentication, error: nil)
    }

    private func authenticate(
        reason: String,
        completion: @escaping (Bool, String?) -> Void
    ) {
        let context = LAContext()
        context.localizedCancelTitle = "취소"

        var error: NSError?
        guard context.canEvaluatePolicy(.deviceOwnerAuthentication, error: &error) else {
            completion(false, "이 기기에는 사용할 수 있는 Face ID, Touch ID 또는 암호 인증이 없어요.")
            return
        }

        context.evaluatePolicy(.deviceOwnerAuthentication, localizedReason: reason) { success, error in
            Task { @MainActor in
                completion(success, success ? nil : appLockErrorMessage(from: error))
            }
        }
    }

    private func appLockErrorMessage(from error: Error?) -> String {
        guard let authError = error as? LAError else {
            return "인증하지 못했어요. 다시 시도해 주세요."
        }

        switch authError.code {
        case .userCancel, .systemCancel, .appCancel:
            return "인증이 취소됐어요."
        case .userFallback:
            return "기기 암호로 다시 인증해 주세요."
        case .biometryNotAvailable, .biometryNotEnrolled, .passcodeNotSet:
            return "기기 설정에서 Face ID, Touch ID 또는 암호를 먼저 등록해 주세요."
        case .biometryLockout:
            return "여러 번 실패해 잠시 잠겼어요. 기기 암호로 인증해 주세요."
        default:
            return "인증하지 못했어요. 다시 시도해 주세요."
        }
    }
}

private struct AppLockGateView: View {
    let message: String?
    let canAuthenticate: Bool
    let onUnlock: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 24) {
            Spacer()
            VStack(alignment: .leading, spacing: 8) {
                Text("SumDiary 잠김")
                    .font(.largeTitle.bold())
                Text("일기와 요약을 보려면 Face ID, Touch ID 또는 기기 암호 인증이 필요해요.")
                    .foregroundStyle(.secondary)
            }
            if let message {
                Text(message)
                    .font(.body)
                    .foregroundStyle(.secondary)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    .background(.thinMaterial)
                    .clipShape(RoundedRectangle(cornerRadius: 8))
            }
            Button("잠금 해제", action: onUnlock)
                .buttonStyle(.borderedProminent)
                .disabled(!canAuthenticate)
            if !canAuthenticate {
                Text("기기 설정에서 Face ID, Touch ID 또는 암호를 먼저 등록해 주세요.")
                    .font(.footnote)
                    .foregroundStyle(.red)
            }
            Spacer()
        }
        .padding(24)
    }
}

#Preview {
    ContentView()
}
