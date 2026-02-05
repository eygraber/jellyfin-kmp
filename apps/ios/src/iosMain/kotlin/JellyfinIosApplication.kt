@file:Suppress("MissingPackageDeclaration")

import androidx.compose.ui.window.ComposeUIViewController
import com.eygraber.jellyfin.app.di.JellyfinIosAppGraph
import com.eygraber.jellyfin.apps.shared.JellyfinAppSession
import dev.zacsweers.metro.createGraphFactory
import platform.UIKit.UIViewController

class JellyfinIosApplication {
  private val appGraph = createGraphFactory<JellyfinIosAppGraph.Factory>().create().apply {
    initializer.initialize()
  }

  fun createViewController(): UIViewController {
    val viewControllerGraph = appGraph.createJellyfinIosViewControllerGraph()
    val navGraph = viewControllerGraph.createJellyfinNavGraph()

    return ComposeUIViewController {
      JellyfinAppSession(
        onDarkMode = {},
        navGraph = navGraph,
      )
    }
  }
}
