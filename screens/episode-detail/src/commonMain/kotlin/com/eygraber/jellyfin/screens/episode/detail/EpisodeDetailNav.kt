package com.eygraber.jellyfin.screens.episode.detail

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
data class EpisodeDetailKey(
  val episodeId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class EpisodeDetailNavEntryProvider(
  override val compositor: EpisodeDetailCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> EpisodeDetailView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface EpisodeDetailGraph {
  val navEntryProvider: EpisodeDetailNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createEpisodeDetailGraph(
      @Provides navigator: EpisodeDetailNavigator,
      @Provides key: EpisodeDetailKey,
    ): EpisodeDetailGraph
  }
}

private typealias Key = EpisodeDetailKey
private typealias View = EpisodeDetailView
private typealias Intent = EpisodeDetailIntent
private typealias Compositor = EpisodeDetailCompositor
private typealias Effects = ViceEffects
private typealias ViewState = EpisodeDetailViewState
