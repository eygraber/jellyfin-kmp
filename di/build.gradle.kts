plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "template.di"
}

dependencies {
  implementation(libs.kotlinInject.runtime)
}
