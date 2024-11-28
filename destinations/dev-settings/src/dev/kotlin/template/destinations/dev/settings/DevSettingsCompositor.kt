package template.destinations.dev.settings

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.DestinationScope

@Inject
@SingleIn(DestinationScope::class)
class DevSettingsCompositor : ViceCompositor<DevSettingsIntent, DevSettingsViewState> {
  @Composable
  override fun composite() = DevSettingsViewState

  override suspend fun onIntent(intent: DevSettingsIntent) {}
}
