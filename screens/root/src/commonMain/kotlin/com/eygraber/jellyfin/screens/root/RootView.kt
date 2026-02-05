package com.eygraber.jellyfin.screens.root

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import com.eygraber.jellyfin.ui.compose.sharedSplashScreenIcon
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.Logo
import com.eygraber.jellyfin.ui.material.theme.JellyfinDarkTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.vice.ViceView
import org.jetbrains.compose.resources.stringResource

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

  JellyfinDarkTheme {
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
    imageVector = JellyfinIcons.Logo,
    contentDescription = stringResource(Res.string.root_cd_splash_icon),
    modifier = Modifier
      .sharedSplashScreenIcon(),
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
  )
}

@Preview
@Composable
private fun RootPreview() {
  JellyfinPreviewTheme {
    RootView(
      state = RootViewState,
      onIntent = {},
    )
  }
}
