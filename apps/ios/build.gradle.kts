import com.eygraber.conventions.kotlin.kmp.spm.registerAssembleXCFrameworkTasksFromFrameworks
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  kmpTargets(
    KmpTarget.Ios,
    ignoreDefaultTargets = true,
    project = project,
  )

  kspDependenciesForAllTargets {
    ksp(libs.kotlinInject.anvilCompiler)
    ksp(libs.kotlinInject.compiler)
  }

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

// needed until https://github.com/google/ksp/issues/2243 is resolved
tasks.all {
  if(name.startsWith("kspKotlinIos")) {
    afterEvaluate {
      setOnlyIf { true }
    }
  }
}
