plugins {
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.licensee)
}

android {
  namespace = "template.apps.shared"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.destinations.devSettings)
      api(projects.destinations.root)
      api(projects.destinations.welcome)

      api(projects.di)

      api(projects.nav)

      api(projects.services.deviceSensors.impl)
      api(projects.services.deviceSensors.public)
      api(projects.services.splashScreen.impl)
      api(projects.services.splashScreen.public)

      api(projects.ui.material)

      api(compose.foundation)
      api(compose.material3)
      api(compose.runtime)
      api(compose.ui)

      api(libs.khronicle)

      api(libs.kotlinInject.runtime)
      api(libs.kotlinInject.anvilRuntime)
      api(libs.kotlinInject.anvilRuntimeOptional)

      implementation(libs.kotlinx.coroutines.core)
    }
  }
}

// whitelist which licenses are able to be used in the app
// new licenses can be added as long as they are permissive
// see https://github.com/cashapp/licensee for configuration options
licensee {
  allow("Apache-2.0")
  allow("MIT")
  allow("Unlicense")
  allowUrl("https://opensource.org/license/mit")

  allowDependency(libs.templateLint)
}
