import SwiftUI

@main
struct SumDiaryApp: App {
    private let googleDriveAuthorizationProvider = GoogleDriveAuthorizationProvider()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    _ = googleDriveAuthorizationProvider.handleOpenURL(url)
                }
                .task {
                    await googleDriveAuthorizationProvider.restorePreviousSignIn()
                }
        }
    }
}
