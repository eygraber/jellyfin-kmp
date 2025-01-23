package template.test.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PaparazziComposeResourcesEffect() {
  CompositionLocalProvider(
    LocalInspectionMode provides true,
  ) {
    PreviewContextConfigurationEffect()
  }
}
