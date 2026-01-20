plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
}

dependencies {
  "androidRuntimeClasspath"(libs.test.compose.uiManifest)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "template.test.utils",
  )

  sourceSets {
    androidMain.dependencies {
      implementation(libs.compose.resources)
      implementation(libs.compose.runtime)
      implementation(libs.compose.uiToolingPreview)

      implementation(libs.bundles.test.composeUi)
      api(libs.test.junit)

      api(libs.test.paparazzi)

      implementation(libs.test.robolectric)
      api(libs.test.robolectric.annotations)
    }

    commonMain.dependencies {
      implementation(libs.compose.runtime)

      implementation(libs.kotlinx.coroutines.core)

      implementation(libs.test.kotlinx.coroutines)
    }
  }
}

dependencies {
  androidRuntimeClasspath(libs.compose.uiToolingPreviewIde)
}
