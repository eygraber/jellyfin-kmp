plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "template.domain.validators"
}

dependencies {
  implementation(libs.compose.runtime)

  implementation(libs.kotlinInject.runtime)

  testImplementation(libs.test.junit)
  testImplementation(libs.test.kotest.assertions.shared)
  testImplementation(libs.test.parameterInjector)
}
