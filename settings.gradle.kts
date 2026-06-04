pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "sumdiary"

include(
    ":app:android",
    ":app:ios",
    ":shared:core-model",
    ":shared:core-util",
    ":shared:core-designsystem",
    ":shared:domain-diary",
    ":shared:domain-auth",
    ":shared:domain-summary",
    ":shared:domain-backup",
    ":shared:data-diary",
    ":shared:data-auth",
    ":shared:data-summary",
    ":shared:data-backup",
    ":shared:feature-entry",
    ":shared:feature-summary",
    ":shared:feature-backup"
)
