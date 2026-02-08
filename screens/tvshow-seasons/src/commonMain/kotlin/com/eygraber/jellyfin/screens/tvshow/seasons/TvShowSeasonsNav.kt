package com.eygraber.jellyfin.screens.tvshow.seasons

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
data class TvShowSeasonsKey(
  val seriesId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class TvShowSeasonsNavEntryProvider(
  override val compositor: TvShowSeasonsCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> TvShowSeasonsView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface TvShowSeasonsGraph {
  val navEntryProvider: TvShowSeasonsNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createTvShowSeasonsGraph(
      @Provides navigator: TvShowSeasonsNavigator,
      @Provides key: TvShowSeasonsKey,
    ): TvShowSeasonsGraph
  }
}

private typealias Key = TvShowSeasonsKey
private typealias View = TvShowSeasonsView
private typealias Intent = TvShowSeasonsIntent
private typealias Compositor = TvShowSeasonsCompositor
private typealias Effects = ViceEffects
private typealias ViewState = TvShowSeasonsViewState
