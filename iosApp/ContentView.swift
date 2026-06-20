import SwiftUI
import SumDiary

struct ContentView: View {
    private let controller = IosSampleController(factory: IosAppFactory())

    @State private var selectedTab = 0
    @State private var showEntryEditor = false
    @State private var entryText = ""
    @State private var summaryText = "요약을 불러와 주세요."
    @State private var summaryStatus = "기간: 오늘"

    var body: some View {
        NavigationStack {
            ZStack(alignment: .bottomTrailing) {
                VStack(spacing: 0) {
                    Picker("탭", selection: $selectedTab) {
                        Text("일기").tag(0)
                        Text("요약").tag(1)
                    }
                    .pickerStyle(.segmented)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)

                    if selectedTab == 0 {
                        diaryView
                    } else {
                        summaryView
                    }
                }

                if selectedTab == 0 {
                    Button {
                        showEntryEditor = true
                    } label: {
                        Image(systemName: "plus")
                            .font(.title2.weight(.semibold))
                            .frame(width: 56, height: 56)
                    }
                    .buttonStyle(.borderedProminent)
                    .clipShape(Circle())
                    .padding(20)
                }
            }
            .navigationTitle("SumDiary")
            .sheet(isPresented: $showEntryEditor) {
                entryEditor
            }
            .onChange(of: selectedTab) { _, newValue in
                if newValue == 1 {
                    Task { await loadDailySummary() }
                }
            }
        }
    }

    private var diaryView: some View {
        List {
            Text("오늘의 샘플 일기")
            Text("AI 요약을 확인해보세요")
        }
        .listStyle(.plain)
    }

    private var summaryView: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(summaryStatus)
            Text(summaryText)
                .frame(maxWidth: .infinity, alignment: .leading)
            Text("감정 태그: neutral")

            Button("오늘 요약") {
                Task { await loadDailySummary() }
            }
            .buttonStyle(.borderedProminent)

            Button("이번 주 요약") {
                Task { await loadWeeklySummary() }
            }
            .buttonStyle(.bordered)

            Spacer()
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    private var entryEditor: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 12) {
                Text("새 일기")
                    .font(.headline)
                TextEditor(text: $entryText)
                    .frame(minHeight: 180)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color.secondary.opacity(0.25))
                    )
                Spacer()
            }
            .padding(16)
            .navigationTitle("새 일기")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("취소") {
                        showEntryEditor = false
                    }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("저장") {
                        saveEntry()
                    }
                }
            }
        }
    }

    private func saveEntry() {
        let text = entryText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !text.isEmpty else { return }
        controller.createSampleEntry(text: text)
        entryText = ""
        showEntryEditor = false
    }

    @MainActor
    private func loadDailySummary() async {
        summaryStatus = "기간: 오늘"
        controller.loadTodaySummary()
        try? await Task.sleep(nanoseconds: 500_000_000)
        summaryText = controller.currentSummaryText()
    }

    @MainActor
    private func loadWeeklySummary() async {
        summaryStatus = "기간: 이번 주"
        controller.loadTodaySummary()
        try? await Task.sleep(nanoseconds: 500_000_000)
        summaryText = controller.currentSummaryText()
    }
}
