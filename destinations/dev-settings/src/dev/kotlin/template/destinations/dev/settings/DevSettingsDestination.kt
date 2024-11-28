package template.destinations.dev.settings

import com.eygraber.vice.ViceEffects
import com.eygraber.vice.nav.ViceDestination
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.DestinationScope
import template.di.scopes.NavScope

@Serializable
data object DevSettingsRoute

@Inject
@SingleIn(DestinationScope::class)
class DevSettingsDestination(
  override val compositor: DevSettingsCompositor,

) : ViceDestination<DevSettingsIntent, DevSettingsCompositor, ViceEffects, DevSettingsViewState>() {
  override val view: DevSettingsView = { state, onIntent -> DevSettingsView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@ContributesSubcomponent(DestinationScope::class)
@SingleIn(DestinationScope::class)
interface DevSettingsDestinationComponent {
  val destination: DevSettingsDestination

  @ContributesSubcomponent.Factory(NavScope::class)
  interface Factory {
    fun createDevSettingsComponent(
      navigator: DevSettingsNavigator,
      route: DevSettingsRoute,
    ): DevSettingsDestinationComponent
  }
}
