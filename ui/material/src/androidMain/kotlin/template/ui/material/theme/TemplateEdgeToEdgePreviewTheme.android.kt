package template.ui.material.theme

import androidx.compose.runtime.Composable
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

@Composable
actual fun TemplateEdgeToEdgePreviewTheme(
  isDarkMode: Boolean,
  content: @Composable () -> Unit,
) {
  EdgeToEdgeTemplate(
    navMode = NavigationMode.Gesture,
    isDarkMode = isDarkMode,
    showInsetsBorder = false,
  ) {
    TemplatePreviewTheme(
      isDarkMode = isDarkMode,
      content = content,
    )
  }
}
