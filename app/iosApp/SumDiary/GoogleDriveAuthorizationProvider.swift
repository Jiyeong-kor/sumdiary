import GoogleSignIn
import UIKit

struct GoogleDriveAuthorization {
    let accessToken: String
    let grantedScopes: Set<String>

    var hasDriveAppDataScope: Bool {
        grantedScopes.contains(GoogleDriveAuthorizationProvider.driveAppDataScope)
    }
}

@MainActor
final class GoogleDriveAuthorizationProvider {
    static let driveAppDataScope = "https://www.googleapis.com/auth/drive.appdata"

    func handleOpenURL(_ url: URL) -> Bool {
        GIDSignIn.sharedInstance.handle(url)
    }

    @discardableResult
    func restorePreviousSignIn() async -> Bool {
        do {
            _ = try await GIDSignIn.sharedInstance.restorePreviousSignIn()
            return true
        } catch {
            return false
        }
    }

    func currentAccessToken() async -> GoogleDriveAuthorization? {
        guard let user = GIDSignIn.sharedInstance.currentUser else {
            return nil
        }
        return await authorization(from: user)
    }

    func requestAccessToken(
        requiredScopes: Set<String> = [GoogleDriveAuthorizationProvider.driveAppDataScope]
    ) async -> GoogleDriveAuthorization? {
        guard let presentingViewController = UIApplication.shared.sumDiaryTopMostViewController else {
            return nil
        }

        do {
            let user = try await authorizedUser(
                requiredScopes: requiredScopes,
                presentingViewController: presentingViewController
            )
            return await authorization(from: user)
        } catch {
            return nil
        }
    }

    private func authorizedUser(
        requiredScopes: Set<String>,
        presentingViewController: UIViewController
    ) async throws -> GIDGoogleUser {
        if let currentUser = GIDSignIn.sharedInstance.currentUser {
            let grantedScopes = Set(currentUser.grantedScopes ?? [])
            let missingScopes = Array(requiredScopes.subtracting(grantedScopes))

            if missingScopes.isEmpty {
                return try await currentUser.refreshTokensIfNeeded()
            }

            return try await currentUser
                .addScopes(missingScopes, presenting: presentingViewController)
                .user
        }

        return try await GIDSignIn.sharedInstance
            .signIn(
                withPresenting: presentingViewController,
                hint: nil,
                additionalScopes: Array(requiredScopes)
            )
            .user
    }

    private func authorization(from user: GIDGoogleUser) async -> GoogleDriveAuthorization? {
        do {
            let refreshedUser = try await user.refreshTokensIfNeeded()
            return GoogleDriveAuthorization(
                accessToken: refreshedUser.accessToken.tokenString,
                grantedScopes: Set(refreshedUser.grantedScopes ?? [])
            )
        } catch {
            return nil
        }
    }
}

private extension UIApplication {
    var sumDiaryTopMostViewController: UIViewController? {
        connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap(\.windows)
            .first { $0.isKeyWindow }?
            .rootViewController?
            .sumDiaryTopMostViewController
    }
}

private extension UIViewController {
    var sumDiaryTopMostViewController: UIViewController {
        if let presentedViewController {
            return presentedViewController.sumDiaryTopMostViewController
        }
        if let navigationController = self as? UINavigationController,
           let visibleViewController = navigationController.visibleViewController {
            return visibleViewController.sumDiaryTopMostViewController
        }
        if let tabBarController = self as? UITabBarController,
           let selectedViewController = tabBarController.selectedViewController {
            return selectedViewController.sumDiaryTopMostViewController
        }
        return self
    }
}
