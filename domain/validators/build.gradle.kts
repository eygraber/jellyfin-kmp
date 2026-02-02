plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
  alias(libs.plugins.testBurst)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "template.domain.validators",
  )

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.runtime)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.test.kotest.assertions.core)
    }
  }
}
