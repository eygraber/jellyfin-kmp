import com.eygraber.jellyfin.gradle.createCmpGroup

plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.metro)
}

val pkg = "com.eygraber.jellyfin.screens.video.player"

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = pkg,
  )

  createCmpGroup()

  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.media3.exoplayer)
      implementation(libs.androidx.media3.ui)
      implementation(libs.androidx.media3.uiCompose)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.test.kotest.assertions.core)
      implementation(libs.test.kotlinx.coroutines)
    }

    commonMain.dependencies {
      api(projects.di)

      implementation(projects.data.playback.public)
      implementation(projects.services.player.public)
      implementation(projects.services.sdk.public)

      implementation(projects.ui.compose)
      implementation(projects.ui.icons)
      implementation(projects.ui.material)

      implementation(libs.compose.foundation)
      implementation(libs.compose.foundationLayout)
      implementation(libs.compose.material3)
      implementation(libs.compose.nav3.runtime)
      implementation(libs.compose.runtime)
      implementation(libs.compose.runtimeAnnotation)
      implementation(libs.compose.ui)
      implementation(libs.compose.uiToolingPreview)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.core)

      implementation(libs.vice.core)
      implementation(libs.vice.nav3)
    }
  }
}
