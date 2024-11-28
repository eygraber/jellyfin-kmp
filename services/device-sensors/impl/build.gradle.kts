plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.ksp)
}

android {
  namespace = "template.services.device.sensors.impl"
}

dependencies {
  api(projects.di)
  api(projects.services.deviceSensors.public)

  implementation(libs.kotlinInject.runtime)
  implementation(libs.kotlinInject.anvilRuntime)

  api(libs.kotlinx.coroutines.core)

  implementation(libs.square.seismic)

  ksp(libs.kotlinInject.anvilCompiler)
}
