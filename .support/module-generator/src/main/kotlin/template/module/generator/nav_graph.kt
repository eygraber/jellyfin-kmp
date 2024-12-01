package template.module.generator

import template.module.generator.utils.insert
import template.module.generator.utils.insertMultiline
import java.io.File

internal fun addToNavGraph(
  projectDir: File,
  featurePackage: String,
  featureName: String,
): Boolean {
  val featureCall = featureName.replaceFirstChar(Char::lowercase)
  val component = "${featureName}DestinationComponent"
  val navigator = "${featureName}Navigator"
  val route = "${featureName}Route"

  val navGraphFile = File(projectDir, "nav/src/main/kotlin/template/nav/TemplateNav.kt")

  navGraphFile.insert(
    newLine = "import $featurePackage.$component",
    intoAlphabetizedSectionWithPrefix = "import ",
  )

  navGraphFile.insert(
    newLine = "import $featurePackage.$navigator",
    intoAlphabetizedSectionWithPrefix = "import ",
  )

  navGraphFile.insert(
    newLine = "import $featurePackage.$route",
    intoAlphabetizedSectionWithPrefix = "import ",
  )

  val factoryExtension =
    """
    |private val TemplateNavComponent.${featureCall}Factory
    |  get() = this as ${featureName}DestinationComponent.Factory
    """.trimMargin()

  navGraphFile.insertMultiline(
    newLine = factoryExtension,
    alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
    lastLineSuffixResolver = "DestinationComponent.Factory",
    intoAlphabetizedSectionWithPrefix = arrayOf("private val TemplateNavComponent."),
  )

  val navGraphCall =
    """
    |  viceComposable<$route> { entry ->
    |    navComponent.${featureCall}Factory.create${featureName}Component(
    |      navigator = $navigator(
    |        onNavigateBack = { navController.popBackStack() },
    |      ),
    |      route = entry.route,
    |    ).destination
    |  }
    """.trimMargin()

  return navGraphFile.insertMultiline(
    newLine = navGraphCall,
    alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
    lastLineSuffixResolver = "  }",
    intoAlphabetizedSectionWithPrefix = arrayOf("  viceComposable<"),
  )
}
