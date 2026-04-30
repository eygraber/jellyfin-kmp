plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
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

  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.media3.exoplayer)
      implementation(libs.androidx.media3.ui.compose)
    }

    jvmMain.dependencies {
      implementation(libs.vlcj)
    }

    commonMain.dependencies {
      api(projects.di)
      api(projects.services.player.public)

      implementation(libs.compose.foundation)
      implementation(libs.compose.foundationLayout)
      implementation(libs.compose.material3)
      implementation(libs.compose.runtime)
      implementation(libs.compose.ui)

      api(libs.kotlinx.coroutines.core)
    }
  }
}
