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
    androidNamespace = "com.eygraber.jellyfin.ui.library.controls",
  )

  sourceSets {
    commonMain.dependencies {
      implementation(projects.data.items.public)

      implementation(projects.ui.icons)

      implementation(libs.compose.foundation)
      implementation(libs.compose.foundationLayout)
      implementation(libs.compose.material3)
      api(libs.compose.runtime)
      implementation(libs.compose.runtimeAnnotation)
      implementation(libs.compose.ui)
    }
  }
}
