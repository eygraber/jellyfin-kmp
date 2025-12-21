package template.app

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window
import org.jetbrains.compose.resources.configureWebResources
import template.app.di.TemplateWebAppComponent
import template.app.di.create
import template.apps.shared.TemplateAppSession

@OptIn(ExperimentalComposeUiApi::class, InternalComposeUiApi::class)
fun main() {
  val appComponent = TemplateWebAppComponent::class.create()
  appComponent.initializer.initialize()

  val sessionComponent = appComponent.createTemplateWebSessionComponent()
  val navComponent = sessionComponent.createTemplateNavComponent()

  configureWebResources {
    resourcePathMapping { path -> "/$path" }
  }

  // https://youtrack.jetbrains.com/issue/CMP-7166
  window.onkeyup = { event ->
    if(event.ctrlKey && event.key == "m") {
      navComponent.shortcutManager.handleKeyEvent(
        KeyEvent(
          key = Key.M,
          type = KeyEventType.KeyUp,
          isCtrlPressed = true,
        ),
      )
    }
  }

  ComposeViewport {
    TemplateAppSession(
      onDarkMode = {},
      navComponent = navComponent,
      modifier = Modifier.padding(horizontal = 700.dp),
    )
  }
}
