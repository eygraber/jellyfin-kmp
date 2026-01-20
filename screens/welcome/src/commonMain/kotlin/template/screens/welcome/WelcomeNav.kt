package template.screens.welcome

import androidx.navigation3.runtime.NavKey
import com.eygraber.vice.ViceEffects
import com.eygraber.vice.nav3.ViceNavEntryProvider
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.NavScope
import template.di.scopes.ScreenScope

@Serializable
data object WelcomeKey : NavKey

@Inject
@SingleIn(ScreenScope::class)
class WelcomeNavEntryProvider(
  override val compositor: WelcomeCompositor,
) : ViceNavEntryProvider<WelcomeKey, WelcomeIntent, WelcomeCompositor, ViceEffects, WelcomeViewState>() {
  override val view: WelcomeView = { state, onIntent -> WelcomeView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@ContributesSubcomponent(ScreenScope::class)
@SingleIn(ScreenScope::class)
interface WelcomeComponent {
  val navEntryProvider: WelcomeNavEntryProvider

  @ContributesSubcomponent.Factory(NavScope::class)
  interface Factory {
    fun createWelcomeComponent(
      navigator: WelcomeNavigator,
      key: WelcomeKey,
    ): WelcomeComponent
  }
}
