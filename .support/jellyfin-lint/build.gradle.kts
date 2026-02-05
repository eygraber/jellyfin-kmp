import com.eygraber.conventions.tasks.deleteRootBuildDirWhenCleaning
import com.eygraber.jellyfin.gradle.findToolchainIfNeeded
import com.google.common.base.CaseFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
  dependencies {
    classpath(libs.buildscript.android)
    classpath(libs.buildscript.androidCacheFix)
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.publish)
  }
}

plugins {
  base
  alias(libs.plugins.conventionsBase)
  alias(libs.plugins.dependencyAnalysisRoot)
  alias(libs.plugins.jellyfinGradle) apply false
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
  }

  detekt {
    plugins(libs.detektCompose)
  }

  kotlin {
    jvmTargetVersion = JvmTarget.fromTarget(libs.versions.jvmTarget.get())
    allWarningsAsErrors = true

    findToolchainIfNeeded(
      javaVersionDir = "../../.github/",
    )?.let { toolchainVersion ->
      this.jdkToolchainVersion = toolchainVersion
      this.jvmDistribution = JvmVendorSpec.AZUL
    }
  }
}

dependencyAnalysis {
  issues {
    all {
      onAny {
        severity("fail")

        // we specifically don't want an explicit dependency on this
        // because it comes from the plugin
        exclude("org.jetbrains.kotlin:kotlin-stdlib")
      }
    }
  }

  structure {
    // Adds the defined aliases in the version catalog to be used when printing advice and rewriting build scripts.
    val versionCatalogName = "libs"
    val versionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named(versionCatalogName)
    versionCatalog.libraryAliases.forEach { alias ->
      map.put(versionCatalog.findLibrary(alias).get().get().toString(), "$versionCatalogName.$alias")
    }

    // Adds the defined aliases for modules to be used when printing advice and rewriting build scripts.
    subprojects.forEach { subproject ->
      val projectAccessor = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, subproject.path.replace(':', '.'))
      map.put(subproject.path, "projects$projectAccessor")
    }
  }
}

// workaround for https://issuetracker.google.com/issues/368378074
if(!file("local.properties").exists() && file("../../local.properties").exists()) {
  file("../../local.properties").copyTo(file("local.properties"))
}
