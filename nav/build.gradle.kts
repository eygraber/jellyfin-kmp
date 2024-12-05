plugins {
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.ksp)
}

android {
  namespace = "template.nav"
}

kotlin {
  defaultKmpTargets(
    project = project,
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

      implementation(compose.animation)
      implementation(compose.material3)
      implementation(compose.runtime)
      implementation(compose.ui)

      implementation(libs.androidx.navigation.common)
      implementation(libs.androidx.navigation.compose)
      implementation(libs.androidx.navigation.runtime)

      implementation(libs.compose.material3Navigation)

      implementation(libs.kotlinInject.runtime)
      implementation(libs.kotlinInject.anvilRuntime)
      implementation(libs.kotlinInject.anvilRuntimeOptional)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.core)

      implementation(libs.vice.nav)
    }
  }
}

// needed until https://github.com/google/ksp/issues/2243 is resolved
tasks.all {
  if(name.startsWith("kspKotlinIos")) {
    afterEvaluate {
      setOnlyIf { true }
    }
  }
}
