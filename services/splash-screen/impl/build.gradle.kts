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
    androidNamespace = "com.eygraber.jellyfin.services.splash.screen.impl",
  )

  androidLibrary {
    androidResources.enable = true
  }

  createCmpGroup()

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
    }
  }
}
