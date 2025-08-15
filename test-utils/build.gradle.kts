import org.jetbrains.compose.compose

plugins {
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "template.test.utils"

  dependencies {
    implementation(compose.components.resources)
    implementation(compose.runtime)
    implementation(compose.uiTooling)

    implementation(libs.bundles.test.composeUi)
    api(libs.test.junit)

    api(libs.test.paparazzi)

    implementation(libs.test.robolectric)
    api(libs.test.robolectric.annotations)

    debugImplementation(libs.test.compose.uiManifest)
  }
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlinx.coroutines.core)

      implementation(libs.test.kotlinx.coroutines)
    }
  }
}
