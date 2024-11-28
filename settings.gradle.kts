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

    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

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
    )
  }
}

rootProject.name = "cmp-app-template"

plugins {
  id("com.eygraber.conventions.settings") version "0.0.81"
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
  id("com.gradle.develocity") version "3.18.2"
}

include(":android")
include(":app")
include(":common")
include(":destinations:dev-settings")
include(":destinations:welcome")
include(":destinations:root")
include(":di")
include(":domain:validators")
include(":nav")
include(":services:device-sensors:impl")
include(":services:device-sensors:public")
include(":services:splash-screen")
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
