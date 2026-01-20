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
      api(projects.di)

      implementation(projects.screens.devSettings)
      implementation(projects.screens.root)
      implementation(projects.screens.welcome)

      api(projects.services.deviceSensors.public)

      implementation(libs.compose.nav3.runtime)
      implementation(libs.compose.nav3.ui)

      implementation(libs.compose.animation)
      implementation(libs.compose.material3)
      implementation(libs.compose.runtime)
      implementation(libs.compose.ui)

      implementation(libs.kotlinInject.runtime)
      implementation(libs.kotlinInject.anvilRuntime)
      implementation(libs.kotlinInject.anvilRuntimeOptional)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.core)

      implementation(libs.vice.nav3)
    }

    commonTest.dependencies {
      implementation(libs.test.kotest.assertions.core)
      implementation(kotlin("test"))
    }
  }
}
