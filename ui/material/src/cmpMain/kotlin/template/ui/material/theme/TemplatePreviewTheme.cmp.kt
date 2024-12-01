package template.ui.material.theme

import androidx.compose.runtime.Composable

@Composable
actual fun TemplateEdgeToEdgePreviewTheme(
  isDarkMode: Boolean,
  content: @Composable () -> Unit,
) {
  TemplatePreviewTheme(
    isDarkMode = isDarkMode,
    content = content,
  )
}
