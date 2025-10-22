import com.eygraber.conventions.Env
import com.eygraber.conventions.repositories.addCommonRepositories

pluginManagement {
  repositories {
    mavenLocal {
      url = uri(".support/.m2")

      content {
        includeGroup("template.gradle")
        includeGroup("template-gradle")
      }
    }

    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }
    }

    mavenCentral()

    maven("https://oss.sonatype.org/content/repositories/snapshots") {
      mavenContent {
        snapshotsOnly()
      }
    }

    maven("https://s01.oss.sonatype.org/content/repositories/snapshots") {
      mavenContent {
        snapshotsOnly()
      }
    }

    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
      content {
        includeGroupByRegex("org\\.jetbrains.*")
      }
    }

    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  // comment this out for now because it doesn't work with KMP js
  // https://youtrack.jetbrains.com/issue/KT-68533/Kotlin-2.0-WasmJs-error-when-using-RepositoriesMode.FAILONPROJECTREPOS
  // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

  repositories {
    mavenLocal {
      url = uri(".support/.m2")

      content {
        includeModule("template.lint", "library")
      }
    }

    addCommonRepositories(
      includeMavenCentral = true,
      includeMavenCentralSnapshots = true,
      includeGoogle = true,
      includeJetbrainsComposeDev = true,
    )
  }
}

rootProject.name = "cmp-app-template"

plugins {
  id("com.eygraber.conventions.settings") version "0.0.88"
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
  id("com.gradle.develocity") version "4.2.2"
}

include(":android")
include(":apps:android")
include(":apps:desktop")
include(":apps:ios")
include(":apps:shared")
include(":apps:web")
include(":common")
include(":compose-preview")
include(":destinations:dev-settings")
include(":destinations:welcome")
include(":destinations:root")
include(":di")
include(":domain:validators")
include(":konsist")
include(":nav")
include(":services:device-sensors:impl")
include(":services:device-sensors:public")
include(":services:splash-screen:impl")
include(":services:splash-screen:public")
include(":test-utils")
include(":ui:compose")
include(":ui:icons")
include(":ui:material")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    publishing.onlyIf { Env.isCI }
    if(Env.isCI) {
      termsOfUseAgree = "yes"
    }
  }
}
