import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.mavenPublish)
}

group = "template.lint"
version = "0.0.1"

android {
  namespace = "template.lint"

  lint {
    checkDependencies = true
  }
}

dependencies {
  lintPublish(projects.checks)
}

mavenPublishing {
  configure(
    AndroidSingleVariantLibrary(
      variant = "release",
      javadocJar = JavadocJar.None(),
      sourcesJar = SourcesJar.Empty(),
    ),
  )
}

publishing {
  repositories {
    maven {
      name = "projectLocal"
      url = uri("${rootDir.absolutePath}/../.m2")
    }
  }
}
