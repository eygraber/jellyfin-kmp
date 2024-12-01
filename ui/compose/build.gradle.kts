plugins {
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "template.ui.compose"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    commonMain.dependencies {
      implementation(compose.animation)
      implementation(compose.foundation)
      api(compose.runtime)
      implementation(compose.ui)

      implementation(libs.kotlinx.coroutines.core)

      implementation(libs.vice.nav)
    }
  }
}
