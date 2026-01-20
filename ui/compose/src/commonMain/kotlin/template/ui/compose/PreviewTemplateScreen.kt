package template.ui.compose

import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(
  name = "Dark",
  fontScale = 1F,
  device = "id:pixel",
  group = "darkMode",
  uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
)
@Preview(
  name = "Light",
  fontScale = 1F,
  device = "id:pixel",
  group = "darkMode",
)
@Preview(
  name = "FontScale2",
  fontScale = 2F,
  device = "id:pixel",
  group = "fontScale",
  uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
)
annotation class PreviewTemplateScreen
