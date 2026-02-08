package com.eygraber.jellyfin.screens.tvshow.episodes

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
data class TvShowEpisodesKey(
  val seriesId: String,
  val seasonId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class TvShowEpisodesNavEntryProvider(
  override val compositor: TvShowEpisodesCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> TvShowEpisodesView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface TvShowEpisodesGraph {
  val navEntryProvider: TvShowEpisodesNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createTvShowEpisodesGraph(
      @Provides navigator: TvShowEpisodesNavigator,
      @Provides key: TvShowEpisodesKey,
    ): TvShowEpisodesGraph
  }
}

private typealias Key = TvShowEpisodesKey
private typealias View = TvShowEpisodesView
private typealias Intent = TvShowEpisodesIntent
private typealias Compositor = TvShowEpisodesCompositor
private typealias Effects = ViceEffects
private typealias ViewState = TvShowEpisodesViewState
