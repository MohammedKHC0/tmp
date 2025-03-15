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
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        val flutterStorageUrl =
            System.getenv("FLUTTER_STORAGE_BASE_URL") ?: "https://storage.googleapis.com"
        maven("$flutterStorageUrl/download.flutter.io")
    }
}

rootProject.name = "Codroid"
include(":app", ":sora-editor-lsp")
// Replace "flutter_module" with whatever package_name you supplied when you ran:
// `$ flutter create -t module [package_name]
val filePath = settingsDir.parentFile.toString() + "/terminal_view/.android/include_flutter.groovy"
apply(from = File(filePath))
