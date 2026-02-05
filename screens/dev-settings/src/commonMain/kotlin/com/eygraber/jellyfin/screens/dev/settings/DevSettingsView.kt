package com.eygraber.jellyfin.screens.dev.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.material.theme.JellyfinEdgeToEdgeModalBottomSheetPreviewTheme
import com.eygraber.vice.ViceView

internal typealias DevSettingsView = ViceView<DevSettingsIntent, DevSettingsViewState>

@Suppress("UNUSED_PARAMETER")
@Composable
internal fun DevSettingsView(
  state: DevSettingsViewState,
  onIntent: (DevSettingsIntent) -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize(),
  ) {
    Text("DevSettings")
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewJellyfinScreen
@Composable
private fun DevSettingsPreview() {
  JellyfinEdgeToEdgeModalBottomSheetPreviewTheme {
    DevSettingsView(
      state = DevSettingsViewState,
      onIntent = {},
    )
  }
}
