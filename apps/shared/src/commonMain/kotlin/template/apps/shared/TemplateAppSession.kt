package template.apps.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import template.nav.TemplateNav
import template.nav.TemplateNavComponent
import template.ui.material.theme.SystemDarkModeOverride
import template.ui.material.theme.TemplateTheme

@Composable
fun TemplateAppSession(
  onDarkMode: @Composable (Boolean) -> Unit,
  navComponent: TemplateNavComponent,
  modifier: Modifier = Modifier,
) {
  val isDarkMode = when(SystemDarkModeOverride.rememberState()) {
    SystemDarkModeOverride.None -> isSystemInDarkTheme()
    SystemDarkModeOverride.Dark -> true
    SystemDarkModeOverride.Light -> false
  }

  onDarkMode(isDarkMode)

  Content(
    isDarkMode = isDarkMode,
    navComponent = navComponent,
    modifier = modifier,
  )
}

@Suppress("ModifierNotUsedAtRoot")
@Composable
private fun Content(
  isDarkMode: Boolean,
  navComponent: TemplateNavComponent,
  modifier: Modifier = Modifier,
) {
  TemplateTheme(
    isDarkMode = isDarkMode,
  ) {
    Surface(
      modifier = Modifier.fillMaxSize(),
    ) {
      Box(modifier = modifier) {
        TemplateNav(
          navComponent = navComponent,
        )
      }
    }
  }
}
