import SwiftUI
import SumDiary

struct ContentView: View {
    private let controller = IosSampleController(factory: IosAppFactory())
    @State private var status = "SumDiary iOS"
    @State private var summary = "샘플 일기를 저장하면 KMP 공유 모듈이 요약을 만듭니다."

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            Text(status)
                .font(.largeTitle.bold())

            Text(summary)
                .font(.body)
                .frame(maxWidth: .infinity, alignment: .leading)

            Button("샘플 일기 저장하고 요약") {
                Task { await createSampleSummary() }
            }
            .buttonStyle(.borderedProminent)
        }
        .padding(24)
    }

    @MainActor
    private func createSampleSummary() async {
        status = "저장 중"
        controller.createSampleEntry(text: "오늘은 Windows에서 만든 IPA를 iPhone에 설치하는 날입니다.")
        try? await Task.sleep(nanoseconds: 500_000_000)

        status = "요약 중"
        controller.loadTodaySummary()
        try? await Task.sleep(nanoseconds: 500_000_000)

        summary = controller.currentSummaryText()
        status = "완료"
    }
}
