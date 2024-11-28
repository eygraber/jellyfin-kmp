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
  namespace = "template.ui.material"
}

dependencies {
  implementation(projects.ui.icons)

  implementation(libs.compose.animation)
  implementation(libs.compose.edgeToEdgePreview)
  implementation(libs.compose.foundation)
  implementation(libs.compose.foundationLayout)
  implementation(libs.compose.material3)
  api(libs.compose.runtime)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
  api(libs.compose.ui.text)
  implementation(libs.compose.ui.unit)

  implementation(libs.kotlinx.coroutines.core)

  implementation(libs.vice.nav)

  debugImplementation(libs.compose.ui.tooling)
  implementation(libs.compose.ui.tooling.preview)
}
