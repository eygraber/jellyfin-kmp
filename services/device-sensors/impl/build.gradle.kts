import template.gradle.createCmpGroup

plugins {
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.ksp)
}

android {
  namespace = "template.services.device.sensors.impl"
}

kotlin {
  defaultKmpTargets(
    project = project,
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

// needed until https://github.com/google/ksp/issues/2243 is resolved
tasks.all {
  if(name.startsWith("kspKotlinIos")) {
    afterEvaluate {
      setOnlyIf { true }
    }
  }
}
