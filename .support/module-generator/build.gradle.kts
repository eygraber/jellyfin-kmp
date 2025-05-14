import com.eygraber.conventions.detekt.configureDetekt
import com.eygraber.conventions.tasks.deleteRootBuildDirWhenCleaning
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
  dependencies {
    classpath(libs.buildscript.compose.compiler)
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.publish)
  }
}

plugins {
  base
  alias(libs.plugins.kotlinJvm) version libs.versions.kotlin
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.conventionsKotlin) version libs.versions.conventions
  alias(libs.plugins.detekt)
}

configureDetekt(
  jvmTargetVersion = JvmTarget.fromTarget(libs.versions.jvmTarget.get()),
  useRootConfigFile = false,
  useProjectConfigFile = false,
  configFiles = files("../../detekt.yml"),
)

deleteRootBuildDirWhenCleaning()

dependencies {
  implementation(compose.desktop.currentOs)

  implementation(compose.material3)

  detektPlugins(libs.detektCompose)
  detektPlugins(libs.detektEygraber.formatting)
  detektPlugins(libs.detektEygraber.style)
}

compose.desktop {
  application {
    mainClass = "template.module.generator.ui.ModuleGeneratorKt"
  }
}

gradleConventions {
  kotlin {
    jvmTargetVersion = JvmTarget.fromTarget(libs.versions.jvmTarget.get())
    allWarningsAsErrors = true
    jdkToolchainVersion = JavaLanguageVersion.of(file("../../.github/.java-version").readText().trim())
    jvmDistribution = JvmVendorSpec.AZUL
  }
}
