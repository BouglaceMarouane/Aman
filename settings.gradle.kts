pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // This is the corrected line:
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Aman"
include(":app")
include(":wear")