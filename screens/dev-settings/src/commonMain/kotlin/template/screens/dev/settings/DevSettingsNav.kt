package template.screens.dev.settings

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
data object DevSettingsKey : NavKey

@Inject
@SingleIn(ScreenScope::class)
class DevSettingsNavEntryProvider(
  override val compositor: DevSettingsCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> DevSettingsView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@ContributesSubcomponent(ScreenScope::class)
@SingleIn(ScreenScope::class)
interface DevSettingsComponent {
  val navEntryProvider: DevSettingsNavEntryProvider

  @ContributesSubcomponent.Factory(NavScope::class)
  interface Factory {
    fun createDevSettingsComponent(
      navigator: DevSettingsNavigator,
      key: DevSettingsKey,
    ): DevSettingsComponent
  }
}

private typealias Key = DevSettingsKey
private typealias View = DevSettingsView
private typealias Intent = DevSettingsIntent
private typealias Compositor = DevSettingsCompositor
private typealias Effects = ViceEffects
private typealias ViewState = DevSettingsViewState
