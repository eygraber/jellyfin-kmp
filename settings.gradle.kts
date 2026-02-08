import com.eygraber.conventions.Env
import com.eygraber.conventions.repositories.addCommonRepositories

pluginManagement {
  repositories {
    mavenLocal {
      url = uri(".support/.m2")

      content {
        includeGroup("com.eygraber.jellyfin.gradle")
        includeGroup("jellyfin-gradle")
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

    maven("https://central.sonatype.com/repository/maven-snapshots/") {
      name = "Central Portal Snapshots"

      mavenContent {
        snapshotsOnly()
      }
    }

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
        includeModule("com.eygraber.jellyfin.lint", "library")
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

rootProject.name = "jellyfin-kmp"

plugins {
  id("com.eygraber.conventions.settings") version "0.0.99"
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
  id("com.gradle.develocity") version "4.3.2"
}

include(":android")
include(":apps:android")
include(":apps:desktop")
include(":apps:ios")
include(":apps:shared")
include(":apps:web")
include(":common")
include(":data:auth:impl")
include(":data:auth:public")
include(":data:items:impl")
include(":data:items:public")
include(":data:server:impl")
include(":data:server:public")
include(":screens:dev-settings")
include(":screens:welcome")
include(":screens:root")
include(":sdk:core")
include(":di")
include(":domain:server:impl")
include(":domain:server:public")
include(":domain:session:impl")
include(":domain:session:public")
include(":domain:validators")
include(":konsist")
include(":nav")
include(":screens:home")
include(":screens:library-movies")
include(":screens:library-music")
include(":screens:library-tvshows")
include(":screens:music-album-tracks")
include(":screens:music-artist-albums")
include(":screens:tvshow-episodes")
include(":screens:tvshow-seasons")
include(":services:api:impl")
include(":services:api:public")
include(":services:database:impl")
include(":services:database:public")
include(":services:device-sensors:impl")
include(":services:logging:impl")
include(":services:logging:public")
include(":services:device-sensors:public")
include(":services:sdk:impl")
include(":services:sdk:public")
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
