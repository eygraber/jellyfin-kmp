import com.eygraber.conventions.kotlin.kmp.spm.registerAssembleXCFrameworkTasksFromFrameworks
import dev.zacsweers.metro.gradle.DiagnosticSeverity
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.metro)
}

kotlin {
  kmpTargets(
    KmpTarget.Ios,
    ignoreDefaultTargets = true,
    project = project,
  )

  targets.withType<KotlinNativeTarget> {
    if(konanTarget.family.isAppleFamily) {
      binaries.framework {
        baseName = "JellyfinKt"
        export(projects.apps.shared)
      }
    }
  }

  project.registerAssembleXCFrameworkTasksFromFrameworks(
    frameworkName = "JellyfinKt",
  )

  sourceSets {
    iosMain.dependencies {
      api(projects.apps.shared)
      implementation(projects.di)
    }
  }
}

gradleConventions {
  kotlin {
    allWarningsAsErrors = false
  }
}

// ideally we'd make this an error so that we can know that we need to clean up the input
// but the key and navigator for screens tend to be unused, and we don't want to remove them
metro {
  unusedGraphInputsSeverity = DiagnosticSeverity.NONE
}
