plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsCompose)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.paparazzi)
}

android {
  namespace = "template.destinations.welcome"
}

dependencies {
  api(projects.di)

  implementation(projects.ui.compose)
  implementation(projects.ui.icons)
  implementation(projects.ui.material)

  implementation(libs.compose.foundation)
  implementation(libs.compose.foundationLayout)
  implementation(libs.compose.material3)
  implementation(libs.compose.runtime)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
  implementation(libs.compose.ui.text)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.compose.ui.unit)

  implementation(libs.kotlinInject.anvilRuntime)
  implementation(libs.kotlinInject.anvilRuntimeOptional)
  implementation(libs.kotlinInject.runtime)

  api(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.serialization.core)

  implementation(libs.vice.core)
  implementation(libs.vice.nav)

  testImplementation(libs.bundles.test.paparazzi)

  debugImplementation(libs.compose.ui.tooling)

  ksp(libs.kotlinInject.anvilCompiler)
}
