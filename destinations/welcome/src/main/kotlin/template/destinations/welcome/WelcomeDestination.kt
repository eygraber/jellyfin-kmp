package template.destinations.welcome

import com.eygraber.vice.nav.ViceDestination
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.DestinationScope
import template.di.scopes.NavScope

@Serializable
data object WelcomeRoute

@Inject
@SingleIn(DestinationScope::class)
class WelcomeDestination(
  override val compositor: WelcomeCompositor,
  override val effects: WelcomeEffects,
) : ViceDestination<WelcomeIntent, WelcomeCompositor, WelcomeEffects, WelcomeViewState>() {
  override val view: WelcomeView = { state, onIntent -> WelcomeView(state, onIntent) }
}

@ContributesSubcomponent(DestinationScope::class)
@SingleIn(DestinationScope::class)
interface WelcomeDestinationComponent {
  val destination: WelcomeDestination

  @ContributesSubcomponent.Factory(NavScope::class)
  interface Factory {
    fun createWelcomeComponent(
      navigator: WelcomeNavigator,
      route: WelcomeRoute,
    ): WelcomeDestinationComponent
  }
}
