@file:Suppress("MissingPackageDeclaration")

import androidx.compose.ui.window.ComposeUIViewController
import dev.zacsweers.metro.createGraphFactory
import platform.UIKit.UIViewController
import template.app.di.TemplateIosAppGraph
import template.apps.shared.TemplateAppSession

class TemplateIosApplication {
  private val appGraph = createGraphFactory<TemplateIosAppGraph.Factory>().create().apply {
    initializer.initialize()
  }

  fun createViewController(): UIViewController {
    val viewControllerGraph = appGraph.createTemplateIosViewControllerGraph()
    val navGraph = viewControllerGraph.createTemplateNavGraph()

    return ComposeUIViewController {
      TemplateAppSession(
        onDarkMode = {},
        navGraph = navGraph,
      )
    }
  }
}
