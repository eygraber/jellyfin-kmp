import com.eygraber.conventions.Env
import com.eygraber.conventions.android.ProductFlavor
import com.eygraber.conventions.kotlin.KotlinFreeCompilerArg
import com.eygraber.conventions.tasks.deleteRootBuildDirWhenCleaning
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import template.gradle.findToolchainIfNeeded

buildscript {
  dependencies {
    classpath(libs.buildscript.android)
    classpath(libs.buildscript.androidCacheFix)
    classpath(libs.buildscript.compose.compiler)
    classpath(libs.buildscript.compose.jetbrains)
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.googleServices)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.kotlinxSerialization)
    classpath(libs.buildscript.ksp)
  }
}

plugins {
  base
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.conventionsBase)
  alias(libs.plugins.dependencyAnalysisRoot)
  alias(libs.plugins.templateGradle) apply false
}

deleteRootBuildDirWhenCleaning()

gradleConventionsDefaults {
  android {
    publishEverything = false

    sdkVersions(
      compileSdk = libs.versions.android.sdk.compile,
      targetSdk = libs.versions.android.sdk.target,
      minSdk = libs.versions.android.sdk.min,
    )

    javaVersionCompatibility(
      sourceCompatibility = libs.versions.jvmTarget,
    )

    useCoreLibraryDesugaring(libs.android.desugar)

    addProductFlavors(
      dimension = "environment",
      flavors = listOf(
        ProductFlavor(name = "dev"),
        ProductFlavor(name = "prod", enabled = Env.isCI),
      ),
    )
  }

  detekt {
    if(!Env.isCI) {
      ignoreAndroidFlavors("prod")
    }

    ignoreAndroidVariants("prodDebug")

    plugins(libs.detektCompose)
    plugins(libs.detektEygraber.formatting)
    plugins(libs.detektEygraber.style)
  }

  projectCommon {
    resolutionVersionSelector = { selector ->
      when(group) {
        "org.jetbrains.kotlin" -> when(selector.configurationName) {
          "detekt" -> {} // https://github.com/detekt/detekt/issues/5021#issuecomment-1178517184
          else -> selector.useVersion(libs.versions.kotlin.get())
        }
      }

      when {
        name.startsWith("kotlinx-coroutines") -> selector.useVersion(libs.versions.kotlinx.coroutines.get())
      }
    }

    projectDependencies {
      implementation(platform(libs.firebase.bom))
      implementation(platform(libs.compose.bom))

      add("runtimeOnly", libs.templateLint)
    }
  }

  kotlin {
    jvmTargetVersion = JvmTarget.fromTarget(libs.versions.jvmTarget.get())
    explicitApiMode = ExplicitApiMode.Disabled
    freeCompilerArgs = setOf(KotlinFreeCompilerArg.AllowExpectActualClasses)
    allWarningsAsErrors = true

    findToolchainIfNeeded()?.let { toolchainVersion ->
      this.jdkToolchainVersion = toolchainVersion
      this.jvmDistribution = JvmVendorSpec.AZUL
    }
  }
}

gradleConventionsKmpDefaults {
  webOptions = KmpTarget.WebOptions(
    isNodeEnabled = false,
    isBrowserEnabled = true,
  )

  targets(
    KmpTarget.Android,
    KmpTarget.Ios,
    KmpTarget.Jvm,
    KmpTarget.WasmJs,
  )
}

dependencyAnalysis {
  useTypesafeProjectAccessors(true)

  issues {
    all {
      onModuleStructure {
        severity("ignore")
      }

      onDuplicateClassWarnings {
        // https://github.com/cashapp/paparazzi/issues/1719
        exclude(
          "org/jetbrains/annotations/NotNull",
          "org/jetbrains/annotations/Nullable",
        )
      }

      onAny {
        severity("fail")

        // we specifically don't want an explicit dependency on this
        // because it comes from the plugin
        exclude("org.jetbrains.kotlin:kotlin-stdlib")

        // this is a weird dependency because of lint
        // so just ignore any issues with it
        exclude("template.lint:library")

        // this is coming from paparazzi
        exclude("com.android.tools.layoutlib:layoutlib")
        exclude("com.android.tools.layoutlib:layoutlib-api")

        // this should be in runtimeOnly but it's easier to have it in implementation
        // because it is for tests it should be fine
        exclude("org.robolectric:robolectric")

        // needed for running ui tests
        exclude("androidx.compose.ui:ui-test-manifest")
      }
    }
  }
}
