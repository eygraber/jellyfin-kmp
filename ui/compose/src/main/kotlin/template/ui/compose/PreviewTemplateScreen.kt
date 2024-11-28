package template.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(
  name = "Light",
  fontScale = 1F,
)
@Preview(
  name = "LightLargeFontScale",
  fontScale = 2F,
  device = "id:pixel",
)
@Preview(
  name = "Dark",
  uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
  fontScale = 1F,
)
@Preview(
  name = "DarkLargeFontScale",
  uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
  fontScale = 2F,
  device = "id:pixel",
)
annotation class PreviewTemplateScreen
