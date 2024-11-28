package template.module.generator

import template.module.generator.utils.insert
import java.io.File

internal fun addModuleToSettings(
  projectDir: File,
  moduleName: String,
): Boolean {
  val settingsFile = File(projectDir, "settings.gradle.kts")

  return settingsFile.insert(
    newLine = "include(\":destinations:$moduleName\")",
    intoAlphabetizedSectionWithPrefix = "include(",
  )
}
