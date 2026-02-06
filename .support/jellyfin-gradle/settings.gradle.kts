pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }
    }

    maven("https://central.sonatype.com/repository/maven-snapshots/") {
      mavenContent {
        snapshotsOnly()
      }
    }

    maven(url = "https://oss.sonatype.org/content/repositories/snapshots") {
      mavenContent {
        snapshotsOnly()
      }
    }

    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots") {
      mavenContent {
        snapshotsOnly()
      }
    }

    mavenCentral()

    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  versionCatalogs {
    create("jellyfinGradleLibs") {
      from(files("../../gradle/libs.versions.toml"))
    }
  }
}

rootProject.name = "jellyfin-gradle"

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
