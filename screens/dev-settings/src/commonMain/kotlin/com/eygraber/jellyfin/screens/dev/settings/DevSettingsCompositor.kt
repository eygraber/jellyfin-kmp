package com.eygraber.jellyfin.screens.dev.settings

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class DevSettingsCompositor : ViceCompositor<DevSettingsIntent, DevSettingsViewState> {
  @Composable
  override fun composite() = DevSettingsViewState

  override suspend fun onIntent(intent: DevSettingsIntent) {}
}
