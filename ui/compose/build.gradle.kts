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
    androidNamespace = "template.ui.compose",
  )

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.animation)
      implementation(libs.compose.foundation)
      implementation(libs.compose.nav3.ui)
      api(libs.compose.runtime)
      implementation(libs.compose.ui)
      implementation(libs.compose.uiToolingPreview)

      implementation(libs.kotlinx.coroutines.core)

      implementation(libs.vice.nav3)
    }
  }
}
