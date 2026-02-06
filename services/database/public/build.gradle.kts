plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.jellyfin.services.database",
  )

  sourceSets {
    commonMain.dependencies {
      api(libs.kotlinx.coroutines.core)
      api(libs.square.sqldelight.runtime)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.test.kotest.assertions.core)
    }
  }
}
