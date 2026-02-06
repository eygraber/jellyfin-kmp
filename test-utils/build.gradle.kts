plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.jellyfin.test.utils",
  )

  sourceSets {
    androidMain.dependencies {
      implementation(libs.compose.resources)
      implementation(libs.compose.runtime)
      implementation(libs.compose.uiToolingPreview)

      implementation(libs.bundles.test.composeUi)
      api(libs.test.junit)

      implementation(libs.test.kotlinx.coroutines)

      api(libs.test.paparazzi)

      implementation(libs.test.robolectric)
      api(libs.test.robolectric.annotations)
    }

    commonMain.dependencies {
      implementation(libs.compose.runtime)

      implementation(libs.kotlinx.coroutines.core)
    }
  }
}

dependencies {
  androidRuntimeClasspath(libs.compose.uiToolingPreviewIde)
  androidRuntimeClasspath(libs.test.compose.uiManifest)
}
