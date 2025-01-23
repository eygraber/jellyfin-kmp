package template.gradle

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion

fun Project.findToolchainIfNeeded(
  javaVersionDir: String = ".github/",
): JavaLanguageVersion? {
  val expectedJavaVersion = file("$javaVersionDir.java-version").readText().trim()
  val isToolchainNeeded = JavaVersion.current() < JavaVersion.toVersion(expectedJavaVersion.toInt())
  return when {
    isToolchainNeeded -> JavaLanguageVersion.of(expectedJavaVersion)
    else -> null
  }
}
