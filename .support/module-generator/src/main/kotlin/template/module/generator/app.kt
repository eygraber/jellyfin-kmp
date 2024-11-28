package template.module.generator

import template.module.generator.utils.insert
import template.module.generator.utils.kebabCaseToCamelCase
import java.io.File

internal fun addModuleToNavDependencies(
  projectDir: File,
  moduleName: String,
): Boolean {
  val moduleProjectName = moduleName.replace(":", ".").kebabCaseToCamelCase(upperCamelCase = false)

  val appGradleBuildFile = File(projectDir, "app/build.gradle.kts")
  appGradleBuildFile.insert(
    newLine = "  implementation(projects.destinations.$moduleProjectName)",
    intoAlphabetizedSectionWithPrefix = "  implementation(projects.destinations.",
  )

  val navGradleBuildFile = File(projectDir, "nav/build.gradle.kts")
  return navGradleBuildFile.insert(
    newLine = "  implementation(projects.destinations.$moduleProjectName)",
    intoAlphabetizedSectionWithPrefix = "  implementation(projects.destinations.",
  )
}
