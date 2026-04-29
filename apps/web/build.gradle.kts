import dev.zacsweers.metro.gradle.DiagnosticSeverity
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.metro)
}

kotlin {
  kmpTargets(
    KmpTarget.WasmJs,
    project = project,
    binaryType = BinaryType.Executable,
    webOptions = KmpTarget.WebOptions(
      isNodeEnabled = false,
      isBrowserEnabled = true,
      moduleName = "jellyfin-wasm",
    ),
    ignoreDefaultTargets = true,
  )

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      commonWebpackConfig {
        outputFileName = "jellyfin-wasm.js"
        experiments += "topLevelAwait"
      }
    }
  }

  sourceSets {
    wasmJsMain.dependencies {
      implementation(projects.apps.shared)

      implementation(libs.compose.resources)
      implementation(libs.compose.ui)
    }
  }
}

// ideally we'd make this an error so that we can know that we need to clean up the input
// but the key and navigator for screens tend to be unused, and we don't want to remove them
metro {
  unusedGraphInputsSeverity = DiagnosticSeverity.NONE
}
