@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// plugin sources for `settings.gradle.kts`
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// package sources for `build.gradle.kts`
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { mavenCentral() }

    versionCatalogs {
        create("libs") {
            from(files("versions.toml"))
        }
    }
}

plugins {
    // allow to resolve missing JVM's
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "chatter"
