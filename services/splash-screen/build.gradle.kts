plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "template.services.splash.screen"
}

dependencies {
  implementation(projects.android)
  api(projects.di)

  implementation(libs.androidx.annotation)
  implementation(libs.androidx.coreKtx)
  api(libs.androidx.appCompat)
  implementation(libs.androidx.splash)

  implementation(libs.kotlinx.coroutines.core)

  implementation(libs.kotlinInject.anvilRuntimeOptional)
  implementation(libs.kotlinInject.runtime)
}
