import java.io.FileReader
import java.util.Properties
 
 val properties = Properties().apply {
     load(FileReader("terminal_view/.android/local.properties"))
 }
 
 val flutterSdkPath = properties.getProperty("flutter.sdk")
 assert(flutterSdkPath != null) {
     "flutter.sdk not set in local.properties"
}

settings.extra.set("flutterSdkPath", flutterSdkPath)
includeBuild("${settings.extra.get("flutterSdkPath")}/packages/flutter_tools/gradle")

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
apply {
    from("flutter_settings.gradle")
}
