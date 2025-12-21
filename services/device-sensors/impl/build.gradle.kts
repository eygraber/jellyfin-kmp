import template.gradle.createCmpGroup

plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.ksp)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "template.services.device.sensors.impl",
  )

  createCmpGroup()

  kspDependenciesForAllTargets {
    ksp(libs.kotlinInject.anvilCompiler)
  }

  sourceSets {
    androidMain.dependencies {
      implementation(libs.square.seismic)
    }

    commonMain.dependencies {
      api(projects.di)
      api(projects.services.deviceSensors.public)

      implementation(libs.kotlinInject.runtime)
      implementation(libs.kotlinInject.anvilRuntime)

      api(libs.kotlinx.coroutines.core)
    }
  }
}
