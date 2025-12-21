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

val pkg = "template.destinations.root"

compose {
  resources {
    packageOfResClass = pkg
  }
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = pkg,
  )

  androidLibrary {
    androidResources.enable = true
  }

  kspDependenciesForAllTargets {
    ksp(libs.kotlinInject.anvilCompiler)
  }

  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.activityCompose)
    }

    commonMain.dependencies {
      api(projects.composePreview)

      api(projects.di)

      api(projects.services.splashScreen.public)

      implementation(projects.ui.compose)
      implementation(projects.ui.icons)
      implementation(projects.ui.material)

      implementation(libs.compose.animation)
      implementation(libs.compose.foundation)
      implementation(libs.compose.material3)
      implementation(libs.compose.resources)
      implementation(libs.compose.runtime)
      implementation(libs.compose.ui)
      implementation(libs.compose.uiToolingPreview)

      implementation(libs.kotlinInject.anvilRuntime)
      implementation(libs.kotlinInject.anvilRuntimeOptional)
      implementation(libs.kotlinInject.runtime)

      api(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.core)

      implementation(libs.vice.core)
      implementation(libs.vice.nav)
    }
  }
}
