import com.eygraber.jellyfin.gradle.createCmpGroup

plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
  alias(libs.plugins.sqldelight)
}

sqldelight {
  databases {
    register("JellyfinDatabase") {
      packageName.set("com.eygraber.jellyfin.services.database.impl")
      dialect(libs.square.sqldelight.dialect)
      generateAsync.set(true)
      deriveSchemaFromMigrations.set(true)
      verifyMigrations.set(true)
    }
  }
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.jellyfin.services.database.impl",
  )

  createCmpGroup()

  sourceSets {
    commonMain.dependencies {
      api(projects.services.database.public)

      implementation(projects.di)

      api(libs.kotlinx.coroutines.core)

      implementation(libs.sqldelight.androidx.driver)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.test.kotest.assertions.core)
      implementation(libs.test.kotlinx.coroutines)
    }

    jvmTest.dependencies {
      implementation(libs.androidx.sqliteBundled)
    }

    androidMain.dependencies {
      implementation(libs.androidx.sqliteBundled)
    }

    iosMain.dependencies {
      implementation(libs.androidx.sqliteBundled)
    }

    jvmMain.dependencies {
      implementation(libs.androidx.sqliteBundled)
    }

    webMain.dependencies {
      implementation(libs.androidx.sqliteWeb)
      implementation(libs.sqldelight.androidx.driverOpfs)
    }
  }
}
