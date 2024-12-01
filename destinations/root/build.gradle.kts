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

val pkg = "template.destinations.root"

android {
  namespace = pkg
}

compose {
  resources {
    packageOfResClass = pkg
  }
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

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

      implementation(compose.animation)
      implementation(compose.components.resources)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.runtime)
      implementation(compose.ui)

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

// needed until https://github.com/google/ksp/issues/2243 is resolved
tasks.all {
  if(name.startsWith("kspKotlinIos")) {
    afterEvaluate {
      setOnlyIf { true }
    }
  }
}
