import com.eygraber.jellyfin.gradle.createCmpGroup

plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.ktorfit)
}

ktorfit {
  compilerPluginVersion.set("2.3.3")
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.jellyfin.services.api.impl",
  )

  createCmpGroup()

  sourceSets {
    androidMain.dependencies {
      implementation(libs.ktor.client.okhttp)
    }

    commonMain.dependencies {
      api(projects.services.api.public)

      api(libs.kotlinx.coroutines.core)
      api(libs.kotlinx.serialization.json)

      api(libs.ktor.client.core)
      api(libs.ktorfit.lib)

      implementation(libs.ktor.client.contentNegotiation)
      implementation(libs.ktor.client.logging)
      implementation(libs.ktor.serialization.json)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.test.kotest.assertions.core)
      implementation(libs.test.kotlinx.coroutines)
      implementation(libs.test.ktor.client.mock)
    }

    iosMain.dependencies {
      implementation(libs.ktor.client.darwin)
    }

    jvmMain.dependencies {
      implementation(libs.ktor.client.java)
    }

    wasmJsMain.dependencies {
      implementation(libs.ktor.client.js)
    }
  }
}
