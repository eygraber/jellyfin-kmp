package template.screens.dev.settings

import androidx.navigation3.runtime.NavKey
import com.eygraber.vice.ViceEffects
import com.eygraber.vice.nav3.ViceNavEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.Serializable
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

@GraphExtension(ScreenScope::class)
interface DevSettingsGraph {
  val navEntryProvider: DevSettingsNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createDevSettingsGraph(
      @Provides navigator: DevSettingsNavigator,
      @Provides key: DevSettingsKey,
    ): DevSettingsGraph
  }
}

private typealias Key = DevSettingsKey
private typealias View = DevSettingsView
private typealias Intent = DevSettingsIntent
private typealias Compositor = DevSettingsCompositor
private typealias Effects = ViceEffects
private typealias ViewState = DevSettingsViewState
