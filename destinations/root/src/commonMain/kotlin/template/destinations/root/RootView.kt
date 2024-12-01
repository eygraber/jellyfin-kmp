package template.destinations.root

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import com.eygraber.vice.ViceView
import org.jetbrains.compose.resources.stringResource
import template.ui.compose.sharedSplashScreenIcon
import template.ui.icons.Logo
import template.ui.icons.TemplateIcons
import template.ui.material.theme.TemplateDarkTheme
import template.ui.material.theme.TemplatePreviewTheme

internal typealias RootView = ViceView<RootIntent, RootViewState>

@Composable
internal expect fun ReportDrawn()

@Suppress("UNUSED_PARAMETER")
@Composable
internal fun RootView(
  state: RootViewState,
  onIntent: (RootIntent) -> Unit,
) {
  ReportDrawn()

  TemplateDarkTheme {
    Surface(
      modifier = Modifier
        .fillMaxSize(),
    ) {
      Box(
        contentAlignment = Alignment.Center,
      ) {
        SplashIcon()
      }
    }
  }
}

@Composable
private fun SplashIcon() {
  Image(
    imageVector = TemplateIcons.Logo,
    contentDescription = stringResource(Res.string.root_cd_splash_icon),
    modifier = Modifier
      .sharedSplashScreenIcon(),
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
  )
}

@Preview
@Composable
private fun RootPreview() {
  TemplatePreviewTheme {
    RootView(
      state = RootViewState,
      onIntent = {},
    )
  }
}
