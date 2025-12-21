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
    androidNamespace = "template.compose.preview",
  )

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.runtime)
    }

    jvmMain.dependencies {
      api(compose.desktop.currentOs)
    }
  }
}
