package template.ui.material.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.eygraber.vice.nav.LocalAnimatedVisibilityScope
import com.eygraber.vice.nav.LocalSharedTransitionScope
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TemplatePreviewTheme(
  isDarkMode: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  TemplateTheme(isDarkMode = isDarkMode) {
    SharedTransitionLayout {
      CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
      ) {
        AnimatedVisibility(
          visible = true,
        ) {
          CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this,
            content = content,
          )
        }
      }
    }
  }
}

@Composable
fun TemplateEdgeToEdgePreviewTheme(
  isDarkMode: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  EdgeToEdgeTemplate(
    navMode = NavigationMode.Gesture,
    showInsetsBorder = false,
  ) {
    TemplatePreviewTheme(
      isDarkMode = isDarkMode,
      content = content,
    )
  }
}
