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
    androidNamespace = "com.eygraber.jellyfin.common",
  )

  sourceSets {
    commonMain.dependencies {
      api(libs.kotlinx.coroutines.core)

      implementation(libs.compose.resources)
      implementation(libs.compose.runtime)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.test.kotest.assertions.core)
      implementation(libs.test.kotlinx.coroutines)
    }
  }
}

compose.resources {
  publicResClass = true
  packageOfResClass = "com.eygraber.jellyfin.common"
  generateResClass = always
}
