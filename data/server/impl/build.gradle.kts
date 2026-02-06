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
    androidNamespace = "com.eygraber.jellyfin.data.server.impl",
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.data.server.public)

      implementation(projects.di)
      implementation(projects.services.database.impl)
      implementation(projects.services.database.public)
      implementation(projects.services.logging.public)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.square.sqldelight.flow)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.test.kotest.assertions.core)
      implementation(libs.test.kotlinx.coroutines)
    }

    jvmTest.dependencies {
      implementation(libs.androidx.sqliteBundled)
      implementation(libs.sqldelight.androidx.driver)
    }
  }
}
