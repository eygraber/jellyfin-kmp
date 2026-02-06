plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.jellyfin.domain.session.impl",
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.domain.session.public)

      implementation(projects.data.auth.public)
      implementation(projects.data.server.public)
      implementation(projects.di)
      implementation(projects.services.logging.public)
      implementation(projects.services.sdk.public)

      implementation(libs.kotlinx.coroutines.core)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.test.kotest.assertions.core)
      implementation(libs.test.kotlinx.coroutines)
      implementation(libs.test.square.turbine)
    }
  }
}
