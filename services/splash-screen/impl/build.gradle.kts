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
  namespace = "template.services.splash.screen.impl"
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
      implementation(projects.android)

      implementation(libs.androidx.annotation)
      implementation(libs.androidx.coreKtx)
      api(libs.androidx.appCompat)
      implementation(libs.androidx.splash)
    }

    commonMain.dependencies {
      implementation(projects.di)

      implementation(projects.services.splashScreen.public)

      implementation(libs.kotlinx.coroutines.core)

      implementation(libs.kotlinInject.anvilRuntime)
      implementation(libs.kotlinInject.anvilRuntimeOptional)
      implementation(libs.kotlinInject.runtime)
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
