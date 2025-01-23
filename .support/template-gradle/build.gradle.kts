import com.eygraber.conventions.detekt.configureDetekt
import com.eygraber.conventions.kotlin.configureKgp
import com.eygraber.conventions.repositories.addCommonRepositories
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
  alias(templateGradleLibs.plugins.conventionsBase)
  alias(templateGradleLibs.plugins.detekt)
  `maven-publish`
}

group = "template.gradle"
version = "0.0.1"

val expectedJavaVersion = file("../../.github/.java-version").readText().trim()
val toolchainVersion: JavaLanguageVersion? = when {
  JavaVersion.current() < JavaVersion.toVersion(expectedJavaVersion.toInt()) ->
    JavaLanguageVersion.of(expectedJavaVersion)

  else -> null
}

val jvmTarget = JvmTarget.fromTarget(templateGradleLibs.versions.jvmTarget.get())

configureKgp(
  jvmTargetVersion = jvmTarget,
  jdkToolchainVersion = toolchainVersion,
  jvmDistribution = JvmVendorSpec.AZUL.takeIf { toolchainVersion != null },
)

configureDetekt(
  jvmTargetVersion = jvmTarget,
  useRootConfigFile = false,
  useProjectConfigFile = false,
  configFiles = files("../../detekt.yml"),
)

repositories {
  addCommonRepositories(
    includeMavenCentral = true,
    includeMavenCentralSnapshots = true,
    includeGoogle = true,
    includeGradlePluginPortal = true,
  )
}

dependencies {
  // this monster is courtesy of https://github.com/gradle/gradle/issues/15383
  implementation(files(templateGradleLibs.javaClass.superclass.protectionDomain.codeSource.location))

  implementation(templateGradleLibs.buildscript.android)
  implementation(templateGradleLibs.buildscript.ejson)
  implementation(templateGradleLibs.buildscript.googleServices)
  implementation(templateGradleLibs.buildscript.kotlin)
  implementation(templateGradleLibs.buildscript.ksp)

  implementation(templateGradleLibs.kotlinx.serialization.json)

  detektPlugins(templateGradleLibs.detektCompose)
  detektPlugins(templateGradleLibs.detektEygraber.formatting)
  detektPlugins(templateGradleLibs.detektEygraber.style)
}

publishing {
  repositories {
    maven {
      name = "projectLocal"
      url = uri("../.m2")
    }
  }
}
