import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN

plugins {
  alias(libs.plugins.buildKonfig)
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.licensee)
}

buildkonfig {
  packageName = "template.apps.shared"

  defaultConfigs {
    buildConfigField(BOOLEAN, "isDev", "false")
  }

  defaultConfigs("dev") {
    buildConfigField(BOOLEAN, "isDev", "true")
  }
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "template.apps.shared",
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

      api(libs.compose.foundation)
      api(libs.compose.material3)
      api(libs.compose.runtime)
      api(libs.compose.ui)

      api(libs.khronicle)

      api(libs.kotlinInject.runtime)
      api(libs.kotlinInject.anvilRuntime)
      api(libs.kotlinInject.anvilRuntimeOptional)

      implementation(libs.kotlinx.coroutines.core)

      api(libs.vice.nav)
    }
  }
}

// whitelist which licenses are able to be used in the app
// new licenses can be added as long as they are permissive
// see https://github.com/cashapp/licensee for configuration options
licensee {
  allow("Apache-2.0")
  allow("MIT")

  // de.drick.compose:edge-to-edge-preview
  allow("Unlicense")

  allowDependency(libs.templateLint)
}
