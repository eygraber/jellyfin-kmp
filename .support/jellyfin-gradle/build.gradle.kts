import com.eygraber.conventions.detekt.configureDetekt2
import com.eygraber.conventions.kotlin.configureKgp
import com.eygraber.conventions.repositories.addCommonRepositories
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
  dependencies {
    classpath(jellyfinGradleLibs.buildscript.publish)
  }
}

plugins {
  `kotlin-dsl`
  alias(jellyfinGradleLibs.plugins.conventionsBase)
  alias(jellyfinGradleLibs.plugins.detekt)
  `maven-publish`
}

group = "com.eygraber.jellyfin.gradle"
version = "0.0.1"

val expectedJavaVersion = file("../../.github/.java-version").readText().trim()
val toolchainVersion: JavaLanguageVersion? = when {
  JavaVersion.current() < JavaVersion.toVersion(expectedJavaVersion.toInt()) ->
    JavaLanguageVersion.of(expectedJavaVersion)

  else -> null
}

val jvmTarget = JvmTarget.fromTarget(jellyfinGradleLibs.versions.jvmTarget.get())

configureKgp(
  jvmTargetVersion = jvmTarget,
  jdkToolchainVersion = toolchainVersion,
  jvmDistribution = JvmVendorSpec.AZUL.takeIf { toolchainVersion != null },
)

configureDetekt2(
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
  implementation(files(jellyfinGradleLibs.javaClass.superclass.protectionDomain.codeSource.location))

  implementation(jellyfinGradleLibs.buildscript.android)
  implementation(jellyfinGradleLibs.buildscript.ejson)
  implementation(jellyfinGradleLibs.buildscript.googleServices)
  implementation(jellyfinGradleLibs.buildscript.kotlin)
  implementation(jellyfinGradleLibs.buildscript.ksp)

  implementation(jellyfinGradleLibs.kotlinx.serialization.json)

  detektPlugins(jellyfinGradleLibs.detektCompose)
}

publishing {
  repositories {
    maven {
      name = "projectLocal"
      url = uri("../.m2")
    }
  }
}
