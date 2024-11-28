plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsCompose)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "template.ui.compose"
}

dependencies {
  implementation(libs.compose.animation)
  implementation(libs.compose.animation.core)
  implementation(libs.compose.foundationLayout)
  api(libs.compose.runtime)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.geometry)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.compose.ui.unit)

  implementation(libs.kotlinx.coroutines.core)

  implementation(libs.vice.nav)
}
