package template.screens.dev.settings

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import me.tatarka.inject.annotations.Inject

@Inject
class DevSettingsCompositor : ViceCompositor<DevSettingsIntent, DevSettingsViewState> {
  @Composable
  override fun composite() = DevSettingsViewState

  override suspend fun onIntent(intent: DevSettingsIntent) {}
}
