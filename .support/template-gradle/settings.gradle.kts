pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
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
    create("templateGradleLibs") {
      from(files("../../gradle/libs.versions.toml"))
    }
  }
}

rootProject.name = "template-gradle"

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
