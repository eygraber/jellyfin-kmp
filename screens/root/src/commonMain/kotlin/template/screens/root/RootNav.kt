package template.screens.root

import androidx.navigation3.runtime.NavKey
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
data object RootKey : NavKey

@Inject
@SingleIn(ScreenScope::class)
class RootNavEntryProvider(
  override val compositor: RootCompositor,
  override val effects: RootEffects,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> RootView(state, onIntent) }
}

@GraphExtension(ScreenScope::class)
interface RootGraph {
  val navEntryProvider: RootNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createRootGraph(
      @Provides navigator: RootNavigator,
      @Provides key: RootKey,
    ): RootGraph
  }
}

private typealias Key = RootKey
private typealias View = RootView
private typealias Intent = RootIntent
private typealias Compositor = RootCompositor
private typealias Effects = RootEffects
private typealias ViewState = RootViewState
