package template.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@Composable
fun WithDensity(
  density: Float = -1F,
  fontScale: Float = -1F,
  content: @Composable () -> Unit,
) {
  val current = LocalDensity.current

  CompositionLocalProvider(
    LocalDensity provides Density(
      density = when(density) {
        -1F -> current.density
        else -> density
      },
      fontScale = when(fontScale) {
        -1F -> current.fontScale
        else -> fontScale
      },
    ),
    content = content,
  )
}
