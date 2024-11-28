package template.destinations.root

import com.eygraber.vice.nav.ViceDestination
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.DestinationScope
import template.di.scopes.NavScope

@Serializable
data object RootRoute

@Inject
@SingleIn(DestinationScope::class)
class RootDestination(
  override val compositor: RootCompositor,
  override val effects: RootEffects,
) : ViceDestination<RootIntent, RootCompositor, RootEffects, RootViewState>() {
  override val view: RootView = { state, onIntent -> RootView(state, onIntent) }
}

@ContributesSubcomponent(DestinationScope::class)
@SingleIn(DestinationScope::class)
interface RootDestinationComponent {
  val destination: RootDestination

  @ContributesSubcomponent.Factory(NavScope::class)
  interface Factory {
    fun createRootComponent(
      navigator: RootNavigator,
      route: RootRoute,
    ): RootDestinationComponent
  }
}
