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
  namespace = "template.ui.icons"
}

dependencies {
  implementation(libs.compose.materialIcons)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.geometry)
  implementation(libs.compose.ui.graphics)
  implementation(libs.compose.ui.unit)
}
