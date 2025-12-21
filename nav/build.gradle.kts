plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.ksp)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "template.nav",
  )

  kspDependenciesForAllTargets {
    ksp(libs.kotlinInject.anvilCompiler)
  }

  sourceSets {
    commonMain.dependencies {
      implementation(projects.destinations.devSettings)
      implementation(projects.destinations.root)
      implementation(projects.destinations.welcome)

      api(projects.di)

      api(projects.services.deviceSensors.public)

      implementation(libs.androidx.navigation.common)
      implementation(libs.androidx.navigation.compose)
      implementation(libs.androidx.navigation.runtime)

      implementation(libs.compose.animation)
      implementation(libs.compose.material3)
      implementation(libs.compose.material3Navigation)
      implementation(libs.compose.runtime)
      implementation(libs.compose.ui)

      implementation(libs.kotlinInject.runtime)
      implementation(libs.kotlinInject.anvilRuntime)
      implementation(libs.kotlinInject.anvilRuntimeOptional)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.core)

      implementation(libs.vice.nav)
    }
  }
}
