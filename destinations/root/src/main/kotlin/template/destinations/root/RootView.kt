package template.destinations.root

import androidx.activity.compose.ReportDrawn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.eygraber.vice.ViceView
import template.ui.compose.PreviewTemplateScreen
import template.ui.compose.sharedSplashScreenIcon
import template.ui.icons.Logo
import template.ui.icons.TemplateIcons
import template.ui.material.theme.TemplateDarkTheme
import template.ui.material.theme.TemplatePreviewTheme

internal typealias RootView = ViceView<RootIntent, RootViewState>

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
    contentDescription = stringResource(R.string.root_cd_splash_icon),
    modifier = Modifier
      .sharedSplashScreenIcon(),
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
  )
}

@PreviewTemplateScreen
@Composable
private fun RootPreview(
  @PreviewParameter(ViewStatePreviewProvider::class)
  state: RootViewState,
) {
  TemplatePreviewTheme {
    RootView(
      state = state,
      onIntent = {},
    )
  }
}
