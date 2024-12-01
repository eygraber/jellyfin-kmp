package template.module.generator

import template.module.generator.utils.div
import java.io.File

internal fun createDestinationModule(
  projectDir: File,
  moduleName: String,
  packageName: String,
  featureName: String,
  shouldIncludeEffects: Boolean,
  shouldGeneratePreview: Boolean,
  shouldGeneratePreviewParameterProvider: Boolean,
) {
  val destinationsDir = File(projectDir, "destinations")
  val moduleDir = File(destinationsDir, moduleName.replace(":", "/")).apply { mkdir() }
  val mainDir = File(moduleDir, "src" / "main").apply { mkdirs() }
  val testDir = File(moduleDir, "src" / "test").apply { mkdirs() }
  val packagePath = packageName.replace(".", File.separator)
  val mainPackageDir = File(mainDir, "kotlin" / packagePath).apply { mkdirs() }
  val testPackageDir = File(testDir, "kotlin" / packagePath).apply { mkdirs() }

  File(moduleDir, "build.gradle.kts").apply {
    if(!exists()) {
      createNewFile()

      val coroutinesDependency = when {
        shouldIncludeEffects -> "  implementation(libs.kotlinx.coroutines.core)\n"
        else -> ""
      }

      writeText(
        """
        |plugins {
        |  alias(libs.plugins.kotlinAndroid)
        |  alias(libs.plugins.conventionsAndroidLibrary)
        |  alias(libs.plugins.conventionsCompose)
        |  alias(libs.plugins.conventionsDetekt)
        |  alias(libs.plugins.conventionsKotlin)
        |  alias(libs.plugins.conventionsProjectCommon)
        |  alias(libs.plugins.dependencyAnalysis)
        |  alias(libs.plugins.kotlinxSerialization)
        |  alias(libs.plugins.ksp)
        |  alias(libs.plugins.paparazzi)
        |}
        |
        |android {
        |  namespace = "$packageName"
        |}
        |
        |dependencies {
        |  api(projects.di)
        |
        |  implementation(projects.ui.compose)
        |  implementation(projects.ui.material)
        |
        |  implementation(libs.compose.foundationLayout)
        |  implementation(libs.compose.material3)
        |  implementation(libs.compose.runtime)
        |  implementation(libs.compose.ui)
        |  implementation(libs.compose.ui.text)
        |  implementation(libs.compose.ui.tooling.preview)
        |
        |  implementation(libs.kotlinInject.anvilRuntime)
        |  implementation(libs.kotlinInject.anvilRuntimeOptional)
        |  implementation(libs.kotlinInject.runtime)
        |
        |$coroutinesDependency  implementation(libs.kotlinx.serialization.core)
        |
        |  implementation(libs.vice.core)
        |  implementation(libs.vice.nav)
        |
        |  testImplementation(libs.bundles.test.paparazzi)
        |
        |  debugImplementation(libs.compose.ui.tooling)
        |
        |  ksp(libs.kotlinInject.anvilCompiler)
        |}
        |
        """.trimMargin(),
      )
    }
  }

  File(moduleDir, "consumer-rules.pro").apply {
    if(!exists()) {
      createNewFile()
      writeText("")
    }
  }

  val destinationName = "${featureName}Destination"
  val compositorName = "${featureName}Compositor"
  val effectsName = when {
    shouldIncludeEffects -> "${featureName}Effects"
    else -> "ViceEffects"
  }
  val intentName = "${featureName}Intent"
  val navigatorName = "${featureName}Navigator"
  val previewName = "${featureName}Preview"
  val routeName = "${featureName}Route"
  val viewName = "${featureName}View"
  val viewStateName = "${featureName}ViewState"
  val viewStatePreviewProviderName = "${viewStateName}PreviewProvider"

  val effectsImports = when {
    shouldIncludeEffects -> emptyArray()
    else -> arrayOf("com.eygraber.vice.ViceEffects")
  }

  val imports = listOf(
    *effectsImports,
    "com.eygraber.vice.nav.ViceDestination",
    "kotlinx.serialization.Serializable",
    "me.tatarka.inject.annotations.Inject",
    "software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent",
    "software.amazon.lastmile.kotlin.inject.anvil.SingleIn",
    "template.di.scopes.DestinationScope",
    "template.di.scopes.NavScope",
  ).sorted()
    .joinToString(separator = "\n") {
      "import $it"
    }

  val destinationParams = when {
    shouldIncludeEffects ->
      """
      |  override val compositor: $compositorName,
      |  override val effects: $effectsName,
      """.trimMargin()

    else ->
      """
      |  override val compositor: $compositorName,
      |
      """.trimMargin()
  }

  val destinationProperties = when {
    shouldIncludeEffects -> "  override val view: $viewName = { state, onIntent -> $viewName(state, onIntent) }"
    else ->
      """
      |  override val view: $viewName = { state, onIntent -> $viewName(state, onIntent) }
      |  override val effects: ViceEffects = ViceEffects.None
      """.trimMargin()
  }

  File(mainPackageDir, "$destinationName.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $packageName
        |
        |$imports
        |
        |@Serializable
        |data object $routeName
        |
        |@Inject
        |@SingleIn(DestinationScope::class)
        |class $destinationName(
        |$destinationParams
        |) : ViceDestination<$intentName, $compositorName, $effectsName, $viewStateName>() {
        |$destinationProperties
        |}
        |
        |@ContributesSubcomponent(DestinationScope::class)
        |@SingleIn(DestinationScope::class)
        |interface ${destinationName}Component {
        |  val destination: $destinationName
        |
        |  @ContributesSubcomponent.Factory(NavScope::class)
        |  interface Factory {
        |    fun create${featureName}Component(
        |      navigator: $navigatorName,
        |      route: $routeName,
        |    ): ${destinationName}Component
        |  }
        |}
        |
        """.trimMargin(),
      )
    }
  }

  File(mainPackageDir, "$navigatorName.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $packageName
        |
        |class $navigatorName(
        |  private val onNavigateBack: () -> Unit,
        |) {
        |  fun navigateBack() {
        |    onNavigateBack()
        |  }
        |}
        |
        """.trimMargin(),
      )
    }
  }

  File(mainPackageDir, "$compositorName.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $packageName
        |
        |import androidx.compose.runtime.Composable
        |import com.eygraber.vice.ViceCompositor
        |import me.tatarka.inject.annotations.Inject
        |
        |@Inject
        |class $compositorName : ViceCompositor<$intentName, $viewStateName> {
        |  @Composable
        |  override fun composite() = $viewStateName
        |
        |  override suspend fun onIntent(intent: $intentName) {}
        |}
        |
        """.trimMargin(),
      )
    }
  }

  if(shouldIncludeEffects) {
    File(mainPackageDir, "$effectsName.kt").apply {
      if(!exists()) {
        createNewFile()
        writeText(
          """
          |package $packageName
          |
          |import com.eygraber.vice.ViceEffects
          |import kotlinx.coroutines.CoroutineScope
          |import me.tatarka.inject.annotations.Inject
          |
          |@Inject
          |class $effectsName : ViceEffects {
          |  override fun CoroutineScope.runEffects() {}
          |}
          |
          """.trimMargin(),
        )
      }
    }
  }

  File(mainPackageDir, "$intentName.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $packageName
        |
        |sealed interface $intentName
        |
        """.trimMargin(),
      )
    }
  }

  File(mainPackageDir, "$viewName.kt").apply {
    if(!exists()) {
      createNewFile()

      val previewImports = when {
        shouldGeneratePreview -> when {
          shouldGeneratePreviewParameterProvider ->
            arrayOf(
              "androidx.compose.ui.tooling.preview.PreviewParameter",
              "template.ui.compose.PreviewTemplateScreen",
            )

          else -> arrayOf(
            "template.ui.compose.PreviewTemplateScreen",
          )
        }

        else -> emptyArray()
      }

      val preview = when {
        shouldGeneratePreview -> when {
          shouldGeneratePreviewParameterProvider ->
            """
            |
            |@PreviewTemplateScreen
            |@Composable
            |private fun $previewName(
            |  @PreviewParameter(ViewStatePreviewProvider::class)
            |  state: $viewStateName,
            |) {
            |  TemplatePreviewTheme {
            |    $viewName(
            |      state = state,
            |      onIntent = {},
            |    )
            |  }
            |}
            |
            """.trimMargin()

          else ->
            """
            |
            |@PreviewTemplateScreen
            |@Composable
            |private fun $previewName() {
            |  TemplatePreviewTheme {
            |    $viewName(
            |      state = ViewState,
            |      onIntent = {},
            |    )
            |  }
            |}
            |
            """.trimMargin()
        }

        else -> ""
      }

      val themeImport = when {
        shouldGeneratePreview || shouldGeneratePreviewParameterProvider ->
          "template.ui.material.theme.TemplatePreviewTheme"

        else -> null
      }

      val viewImports =
        listOfNotNull(
          "androidx.compose.foundation.layout.Box",
          "androidx.compose.foundation.layout.fillMaxSize",
          "androidx.compose.foundation.layout.padding",
          "androidx.compose.material3.Scaffold",
          "androidx.compose.material3.Text",
          "androidx.compose.runtime.Composable",
          "androidx.compose.ui.Modifier",
          "com.eygraber.vice.ViceView",
          themeImport,
          *previewImports,
        ).sorted()
          .joinToString(separator = "\n") {
            "import $it"
          }

      writeText(
        """
        |package $packageName
        |
        |$viewImports
        |
        |internal typealias $viewName = ViceView<$intentName, $viewStateName>
        |
        |@Suppress("UNUSED_PARAMETER")
        |@Composable
        |internal fun $viewName(
        |  state: $viewStateName,
        |  onIntent: ($intentName) -> Unit,
        |) {
        |  Scaffold { contentPadding ->
        |    Box(
        |      modifier = Modifier
        |        .fillMaxSize()
        |        .padding(contentPadding),
        |    ) {
        |      Text("$featureName")
        |    }
        |  }
        |}
        |$preview
        """.trimMargin(),
      )
    }
  }

  File(mainPackageDir, "$viewStateName.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $packageName
        |
        |import androidx.compose.runtime.Immutable
        |
        |@Immutable
        |data object $viewStateName
        |
        """.trimMargin(),
      )
    }
  }

  if(shouldGeneratePreviewParameterProvider) {
    File(mainPackageDir, "$viewStatePreviewProviderName.kt").apply {
      if(!exists()) {
        createNewFile()
        writeText(
          """
          |@file:Suppress("ktlint:standard:argument-list-wrapping", "ktlint:standard:max-line-length")
          |
          |package $packageName
          |
          |import androidx.compose.ui.tooling.preview.PreviewParameterProvider
          |
          |internal class $viewStatePreviewProviderName : PreviewParameterProvider<$viewStateName> {
          |  override val values = sequenceOf(
          |    $viewStateName,
          |  )
          |}
          |
          |internal typealias ViewStatePreviewProvider = $viewStatePreviewProviderName
          |
          """.trimMargin(),
        )
      }
    }
  }

  File(testPackageDir, "${featureName}ScreenshotTest.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $packageName
        |
        |import app.cash.paparazzi.DeviceConfig
        |import app.cash.paparazzi.Paparazzi
        |import com.google.testing.junit.testparameterinjector.TestParameter
        |import com.google.testing.junit.testparameterinjector.TestParameterInjector
        |import org.junit.Rule
        |import org.junit.Test
        |import org.junit.runner.RunWith
        |import template.ui.compose.WithDensity
        |import template.ui.material.theme.TemplateEdgeToEdgePreviewTheme
        |
        |@RunWith(TestParameterInjector::class)
        |class ${featureName}ScreenshotTest {
        |  @get:Rule
        |  val paparazzi = Paparazzi(
        |    deviceConfig = DeviceConfig.PIXEL,
        |  )
        |
        |  @TestParameter
        |  private var isDarkMode: Boolean = false
        |
        |  @TestParameter("1", "2")
        |  private var fontScale: Float = 1F
        |
        |  @Test
        |  fun screenshot() {
        |    paparazzi.snapshot {
        |      TemplateEdgeToEdgePreviewTheme(isDarkMode = isDarkMode) {
        |        WithDensity(fontScale = fontScale) {
        |          $viewName(
        |            state = $viewStateName,
        |            onIntent = {},
        |          )
        |        }
        |      }
        |    }
        |  }
        |}
        |
        """.trimMargin(),
      )
    }
  }
}
