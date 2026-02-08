package com.eygraber.jellyfin.screens.library.tvshows

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
data class TvShowsLibraryKey(
  val libraryId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class TvShowsLibraryNavEntryProvider(
  override val compositor: TvShowsLibraryCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> TvShowsLibraryView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface TvShowsLibraryGraph {
  val navEntryProvider: TvShowsLibraryNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createTvShowsLibraryGraph(
      @Provides navigator: TvShowsLibraryNavigator,
      @Provides key: TvShowsLibraryKey,
    ): TvShowsLibraryGraph
  }
}

private typealias Key = TvShowsLibraryKey
private typealias View = TvShowsLibraryView
private typealias Intent = TvShowsLibraryIntent
private typealias Compositor = TvShowsLibraryCompositor
private typealias Effects = ViceEffects
private typealias ViewState = TvShowsLibraryViewState
