import SumDiary
import SwiftUI

struct ContentView: View {
    private let controller = IosSampleController()

    @State private var entryText = ""
    @State private var summaryText = "오늘의 일기를 작성하고 요약을 불러오세요."

    var body: some View {
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
                        controller.loadTodaySummary()
                        summaryText = controller.currentSummaryText()
                    }
                }
            }
            .navigationTitle("SumDiary")
        }
    }
}

#Preview {
    ContentView()
}
