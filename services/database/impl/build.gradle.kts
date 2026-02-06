import com.eygraber.jellyfin.gradle.createCmpGroup

plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.sqldelight)
}

sqldelight {
  databases {
    create("JellyfinDatabase") {
      packageName.set("com.eygraber.jellyfin.services.database.impl")
      dialect(libs.square.sqldelight.dialect)
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

      api(libs.kotlinx.coroutines.core)

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

    androidMain.dependencies {
      implementation(libs.androidx.sqliteBundled)
      implementation(libs.sqldelight.androidx.driver)
    }

    iosMain.dependencies {
      implementation(libs.androidx.sqliteBundled)
      implementation(libs.sqldelight.androidx.driver)
    }

    jvmMain.dependencies {
      implementation(libs.androidx.sqliteBundled)
      implementation(libs.sqldelight.androidx.driver)
    }

    // WasmJs database support requires SQLDelight's async web worker driver
    // which needs generateAsync=true. This will be implemented in a future issue.
    // For now, WasmJs uses a stub driver.
  }
}
