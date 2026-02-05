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
    androidNamespace = "com.eygraber.jellyfin.services.device.sensors.impl",
  )

  createCmpGroup()

  sourceSets {
    androidMain.dependencies {
      implementation(libs.square.seismic)
    }

    commonMain.dependencies {
      api(projects.di)
      api(projects.services.deviceSensors.public)

      api(libs.kotlinx.coroutines.core)
    }
  }
}
