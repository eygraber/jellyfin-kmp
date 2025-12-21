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
    androidNamespace = "template.common",
  )

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.resources)
      implementation(libs.compose.runtime)
    }
  }
}

compose.resources {
  publicResClass = true
  packageOfResClass = "template.common"
  generateResClass = always
}
