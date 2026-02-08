import com.eygraber.jellyfin.gradle.createCmpGroup

plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.jellyfin.services.player.impl",
  )

  createCmpGroup()

  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.media3.exoplayer)
      implementation(libs.androidx.media3.session)
    }

    commonMain.dependencies {
      api(projects.di)
      api(projects.services.player.public)

      api(libs.kotlinx.coroutines.core)
    }
  }
}
