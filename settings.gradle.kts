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

    ":shared:core:model",
    ":shared:core:util",
    ":shared:core:designsystem",

    ":shared:domain:diary",
    ":shared:domain:auth",
    ":shared:domain:summary",

    ":shared:data:diary",
    ":shared:data:auth",
    ":shared:data:summary",

    ":shared:feature:diary",
    ":shared:feature:auth",
    ":shared:feature:summary"
)
