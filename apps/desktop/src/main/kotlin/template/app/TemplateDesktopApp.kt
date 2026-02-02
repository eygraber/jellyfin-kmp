package template.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import dev.zacsweers.metro.createGraphFactory
import template.app.di.TemplateDesktopAppGraph
import template.apps.shared.TemplateAppSession
import java.awt.Dimension

fun main() {
  val appGraph = createGraphFactory<TemplateDesktopAppGraph.Factory>().create()
  appGraph.initializer.initialize()

  val sessionGraph = appGraph.createTemplateDesktopSessionGraph()
  val navGraph = sessionGraph.createTemplateNavGraph()

  singleWindowApplication(
    state = WindowState(
      width = 400.dp,
      height = 800.dp,
    ),
    onPreviewKeyEvent = { event ->
      event.type == KeyEventType.KeyUp && navGraph.shortcutManager.handleKeyEvent(event)
    },
  ) {
    WindowMinSizeEffect(window)

    TemplateAppSession(
      onDarkMode = {},
      navGraph = navGraph,
    )
  }
}

@Composable
private fun WindowMinSizeEffect(
  window: ComposeWindow,
) {
  val minimumWindowSize = with(LocalDensity.current) {
    val minSize = DpSize(
      width = 400.dp,
      height = 800.dp,
    )
    remember(density, minSize) { Dimension(minSize.width.roundToPx(), minSize.height.roundToPx()) }
  }

  DisposableEffect(window, minimumWindowSize) {
    window.minimumSize = minimumWindowSize

    onDispose {}
  }
}
