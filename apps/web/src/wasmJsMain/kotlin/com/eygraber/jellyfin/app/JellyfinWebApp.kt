package com.eygraber.jellyfin.app

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.eygraber.jellyfin.app.di.JellyfinWebAppGraph
import com.eygraber.jellyfin.apps.shared.JellyfinAppSession
import dev.zacsweers.metro.createGraphFactory
import kotlinx.browser.window
import org.jetbrains.compose.resources.configureWebResources

@OptIn(ExperimentalComposeUiApi::class, InternalComposeUiApi::class)
fun main() {
  val appGraph = createGraphFactory<JellyfinWebAppGraph.Factory>().create()
  appGraph.initializer.initialize()

  val sessionGraph = appGraph.createJellyfinWebSessionGraph()
  val navGraph = sessionGraph.createJellyfinNavGraph()

  configureWebResources {
    resourcePathMapping { path -> "/$path" }
  }

  // https://youtrack.jetbrains.com/issue/CMP-7166
  window.onkeyup = { event ->
    if(event.ctrlKey && event.key == "m") {
      navGraph.shortcutManager.handleKeyEvent(
        KeyEvent(
          key = Key.M,
          type = KeyEventType.KeyUp,
          isCtrlPressed = true,
        ),
      )
    }
  }

  ComposeViewport {
    JellyfinAppSession(
      onDarkMode = {},
      navGraph = navGraph,
      modifier = Modifier.padding(horizontal = 700.dp),
    )
  }
}
