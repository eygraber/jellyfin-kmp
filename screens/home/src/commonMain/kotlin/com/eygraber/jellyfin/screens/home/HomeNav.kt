package com.eygraber.jellyfin.screens.home

import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.di.scopes.NavScope
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.vice.ViceEffects
import com.eygraber.vice.nav3.ViceNavEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

@Inject
@SingleIn(ScreenScope::class)
class HomeNavEntryProvider(
  override val compositor: HomeCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> HomeView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface HomeGraph {
  val navEntryProvider: HomeNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createHomeGraph(
      @Provides navigator: HomeNavigator,
      @Provides key: HomeKey,
    ): HomeGraph
  }
}

private typealias Key = HomeKey
private typealias View = HomeView
private typealias Intent = HomeIntent
private typealias Compositor = HomeCompositor
private typealias Effects = ViceEffects
private typealias ViewState = HomeViewState
