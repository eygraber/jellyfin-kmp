import com.eygraber.conventions.kotlin.kmp.spm.registerAssembleXCFrameworkTasksFromFrameworks
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
        baseName = "TemplateKt"
        export(projects.apps.shared)
      }
    }
  }

  project.registerAssembleXCFrameworkTasksFromFrameworks(
    frameworkName = "TemplateKt",
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
