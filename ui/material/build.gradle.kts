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
    androidNamespace = "template.ui.material",
  )

  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.annotation)
      implementation(libs.compose.edgeToEdgePreview)
    }

    commonMain.dependencies {
      implementation(projects.composePreview)
      implementation(projects.ui.icons)

      implementation(libs.compose.animation)
      implementation(libs.compose.foundation)
      implementation(libs.compose.material3)
      api(libs.compose.runtime)
      implementation(libs.compose.ui)
      implementation(libs.compose.uiToolingPreview)

      implementation(libs.vice.nav)
    }
  }
}
