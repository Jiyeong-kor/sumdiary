import Foundation
import SumDiary

#if canImport(FoundationModels)
import FoundationModels
#endif

final class FoundationModelsSummarizerProvider: IosNativeSummarizer {
    var isSupported: Bool {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            return SystemLanguageModel.default.isAvailable
        }
        #endif
        return false
    }

    func summarize(text: String, completion: @escaping (String?) -> Void) {
        guard isSupported else {
            completion(nil)
            return
        }

        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            Task {
                do {
                    let summary = try await Self.generateSummary(from: text)
                    completion(summary.trimmingCharacters(in: .whitespacesAndNewlines))
                } catch {
                    completion(nil)
                }
            }
            return
        }
        #endif

        completion(nil)
    }

    #if canImport(FoundationModels)
    @available(iOS 26.0, *)
    private static func generateSummary(from text: String) async throws -> String {
        let instructions = """
        You summarize private diary entries on device.
        Respond in Korean.
        Do not diagnose, judge, or infer mental health.
        Return one concise paragraph only.
        """
        let prompt = """
        아래 일기들을 하루 회고용으로 요약해 주세요.
        사용자가 직접 확인할 수 있는 사건과 감정 흐름만 조심스럽게 정리하세요.

        \(text)
        """
        let session = LanguageModelSession(instructions: instructions)
        let response = try await session.respond(to: prompt)
        return response.content
    }
    #endif
}
