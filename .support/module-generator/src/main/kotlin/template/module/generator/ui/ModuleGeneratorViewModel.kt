package template.module.generator.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import template.module.generator.addModuleToNavDependencies
import template.module.generator.addModuleToSettings
import template.module.generator.addToNavGraph
import template.module.generator.createDestinationModule
import template.module.generator.utils.camelCaseToDotCase
import template.module.generator.utils.camelCaseToKebabCase
import java.io.File
import kotlin.system.exitProcess

private const val PackageNamePrefix = "template.destinations."

private val ModuleNameRegex = Regex("^([a-z]+(-))*[a-z]+(?::([a-z]+(-))*[a-z]+)*\$")
private val PackageNameRegex = Regex("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)*[a-z0-9_]*\$")
private val FeatureNameRegex = Regex("[A-Z][A-Za-z0-9]*")

internal class ModuleGeneratorViewModel {
  private val projectDir = File(System.getProperty("user.dir")).run {
    if(name == "module-generator") {
      parentFile.parentFile
    }
    else {
      this
    }
  }

  private val destinationsDir = File(projectDir, "destinations")

  var isProjectDirValid = projectDir.name == "android-app-template"
  var shouldGenerateViceEffects by mutableStateOf(false)
  var shouldInferModuleName by mutableStateOf(true)
  var shouldInferPackageName by mutableStateOf(true)
  var shouldGeneratePreview by mutableStateOf(true)

  private var shouldGeneratePreviewParameterProviderInternal by mutableStateOf(true)
  val shouldGeneratePreviewParameterProvider by derivedStateOf {
    shouldGeneratePreviewParameterProviderInternal && shouldGeneratePreview
  }
  var featureName by mutableStateOf("")
  var featureNameError by mutableStateOf<String?>(null)
  var doesModuleAlreadyExist by mutableStateOf(false)
  var moduleNamePrefix by mutableStateOf(":destinations:")
  var moduleName by mutableStateOf("")
  var moduleNameError by mutableStateOf<String?>(null)
  var packageName by mutableStateOf(PackageNamePrefix)
  var packageNameError by mutableStateOf<String?>(null)

  var isProgressShowing by mutableStateOf(false)
  var progressText by mutableStateOf("")

  val isGenerationEnabled by derivedStateOf {
    moduleName.isNotEmpty() && moduleNameError == null &&
      packageName.isNotEmpty() && packageNameError == null &&
      featureName.isNotEmpty() && featureNameError == null
  }

  fun onGenerateViceEffectsChange(newValue: Boolean) {
    shouldGenerateViceEffects = newValue
  }

  fun onInferPackageNameChange(newValue: Boolean) {
    shouldInferPackageName = newValue

    if(newValue) {
      onPackageNameChange(generateInferredPackageName())
    }
  }

  fun onInferModuleNameChange(newValue: Boolean) {
    shouldInferModuleName = newValue

    updateModuleNamePrefix()
    if(newValue) {
      onModuleNameChange(generateInferredModuleName())
    }
    else {
      onModuleNameChange(moduleName.removePrefix(":"))
    }
  }

  fun onGeneratePreviewChange(newValue: Boolean) {
    shouldGeneratePreview = newValue
  }

  fun onGeneratePreviewParameterProviderChange(newValue: Boolean) {
    shouldGeneratePreviewParameterProviderInternal = newValue
  }

  fun onFeatureNameChange(newFeatureName: String) {
    featureName = newFeatureName.trim()

    val isValid = featureName.matches(FeatureNameRegex)

    if(isValid) {
      if(shouldInferPackageName) {
        onPackageNameChange(generateInferredPackageName())
      }

      if(shouldInferModuleName) {
        onModuleNameChange(generateInferredModuleName())
      }
    }

    featureNameError = when {
      isValid -> null

      else -> when {
        featureName.isBlank() -> "Feature name must not be empty"

        else ->
          """
          |Feature name:
          |  • must begin with an uppercase character
          |  • can only contain characters or digits
          """.trimMargin()
      }
    }
  }

  fun onModuleNameChange(newModuleName: String) {
    moduleName = newModuleName.trim()

    val isValid = moduleName.matches(ModuleNameRegex)

    doesModuleAlreadyExist = when {
      isValid -> with(File(destinationsDir, moduleName)) {
        exists() && isDirectory
      }

      else -> false
    }

    moduleNameError = when {
      isValid -> null

      else -> when {
        moduleName.isBlank() -> "Module name must not be empty"

        else ->
          """
          |Module name:
          |  • must begin and end with a lowercase character
          |  • can't have consecutive '-'
          |  • can only contain lowercase characters and '-'
          """.trimMargin()
      }
    }
  }

  fun onPackageNameChange(newPackageName: String) {
    packageName = newPackageName.trim()

    val isValid = packageName.matches(PackageNameRegex)

    packageNameError = when {
      isValid -> null

      else -> when {
        packageName.isBlank() -> "Package name must not be empty"

        else ->
          """
          |Package name:
          |  • must begin with a lowercase character
          |  • can only contain lowercase characters, digits, '.', and '_'
          |  • can't have consecutive '.'
          |  • can't end with a '.'
          """.trimMargin()
      }
    }
  }

  fun generate() {
    isProgressShowing = true

    @OptIn(DelicateCoroutinesApi::class)
    @Suppress("InjectDispatcher")
    GlobalScope.launch(Dispatchers.IO) {
      progressText = "Generating Files"

      createDestinationModule(
        projectDir = projectDir,
        moduleName = moduleName,
        packageName = packageName,
        featureName = featureName,
        shouldIncludeEffects = shouldGenerateViceEffects,
        shouldGeneratePreview = shouldGeneratePreview,
        shouldGeneratePreviewParameterProvider = shouldGeneratePreviewParameterProviderInternal,
      )

      addModuleToSettings(
        projectDir = projectDir,
        moduleName = moduleName,
      )

      addModuleToNavDependencies(projectDir, moduleName)

      addToNavGraph(
        projectDir = projectDir,
        featurePackage = packageName,
        featureName = featureName,
      )

      val projectRoot = destinationsDir.parentFile
      val gradleTask = ":destinations:$moduleName:recordPaparazziDevDebug"

      progressText = "Running ./gradlew $gradleTask"

      ProcessBuilder(
        File(projectRoot, "gradlew").absolutePath,
        "-p",
        projectRoot.absolutePath,
        gradleTask,
      ).inheritIO()
        .start()
        .waitFor()

      exitProcess(0)
    }
  }

  private fun generateInferredPackageName() =
    "$PackageNamePrefix${featureName.camelCaseToDotCase()}"

  private fun generateInferredModuleName() = featureName.camelCaseToKebabCase()

  private fun updateModuleNamePrefix() {
    moduleNamePrefix = ":destinations:"
  }
}
