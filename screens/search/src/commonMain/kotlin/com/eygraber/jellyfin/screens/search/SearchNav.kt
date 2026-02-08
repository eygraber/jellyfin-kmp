package com.eygraber.jellyfin.screens.search

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
data object SearchKey : NavKey

@Inject
@SingleIn(ScreenScope::class)
class SearchNavEntryProvider(
  override val compositor: SearchCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> SearchView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface SearchGraph {
  val navEntryProvider: SearchNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createSearchGraph(
      @Provides navigator: SearchNavigator,
      @Provides key: SearchKey,
    ): SearchGraph
  }
}

private typealias Key = SearchKey
private typealias View = SearchView
private typealias Intent = SearchIntent
private typealias Compositor = SearchCompositor
private typealias Effects = ViceEffects
private typealias ViewState = SearchViewState
