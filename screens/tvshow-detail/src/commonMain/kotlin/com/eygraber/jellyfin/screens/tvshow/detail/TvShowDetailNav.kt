package com.eygraber.jellyfin.screens.tvshow.detail

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
data class TvShowDetailKey(
  val seriesId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class TvShowDetailNavEntryProvider(
  override val compositor: TvShowDetailCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> TvShowDetailView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface TvShowDetailGraph {
  val navEntryProvider: TvShowDetailNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createTvShowDetailGraph(
      @Provides navigator: TvShowDetailNavigator,
      @Provides key: TvShowDetailKey,
    ): TvShowDetailGraph
  }
}

private typealias Key = TvShowDetailKey
private typealias View = TvShowDetailView
private typealias Intent = TvShowDetailIntent
private typealias Compositor = TvShowDetailCompositor
private typealias Effects = ViceEffects
private typealias ViewState = TvShowDetailViewState
