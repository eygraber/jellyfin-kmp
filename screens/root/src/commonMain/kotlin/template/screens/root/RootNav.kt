package template.screens.root

import androidx.navigation3.runtime.NavKey
import com.eygraber.vice.nav3.ViceNavEntryProvider
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.NavScope
import template.di.scopes.ScreenScope

@Serializable
data object RootKey : NavKey

@Inject
@SingleIn(ScreenScope::class)
class RootNavEntryProvider(
  override val compositor: RootCompositor,
  override val effects: RootEffects,
) : ViceNavEntryProvider<RootKey, RootIntent, RootCompositor, RootEffects, RootViewState>() {
  override val view: RootView = { state, onIntent -> RootView(state, onIntent) }
}

@ContributesSubcomponent(ScreenScope::class)
@SingleIn(ScreenScope::class)
interface RootComponent {
  val navEntryProvider: RootNavEntryProvider

  @ContributesSubcomponent.Factory(NavScope::class)
  interface Factory {
    fun createRootComponent(
      navigator: RootNavigator,
      key: RootKey,
    ): RootComponent
  }
}
