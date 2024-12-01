import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  kmpTargets(
    KmpTarget.WasmJs,
    project = project,
    binaryType = BinaryType.Executable,
    webOptions = KmpTarget.WebOptions(
      isNodeEnabled = false,
      isBrowserEnabled = true,
      moduleName = "template-wasm",
    ),
    ignoreDefaultTargets = true,
  )

  kspDependenciesForAllTargets {
    ksp(libs.kotlinInject.anvilCompiler)
    ksp(libs.kotlinInject.compiler)
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      commonWebpackConfig {
        outputFileName = "template-wasm.js"
        experiments += "topLevelAwait"
      }
    }
  }

  sourceSets {
    wasmJsMain.dependencies {
      implementation(projects.apps.shared)

      implementation(compose.components.resources)
      implementation(compose.ui)
    }
  }
}

// this can be removed after 2.1.10
// https://youtrack.jetbrains.com/issue/CMP-5680
gradleConventions {
  kotlin {
    allWarningsAsErrors = false
  }
}
