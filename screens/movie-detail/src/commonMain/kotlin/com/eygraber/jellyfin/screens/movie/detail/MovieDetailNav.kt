package com.eygraber.jellyfin.screens.movie.detail

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
data class MovieDetailKey(
  val movieId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class MovieDetailNavEntryProvider(
  override val compositor: MovieDetailCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> MovieDetailView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface MovieDetailGraph {
  val navEntryProvider: MovieDetailNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createMovieDetailGraph(
      @Provides navigator: MovieDetailNavigator,
      @Provides key: MovieDetailKey,
    ): MovieDetailGraph
  }
}

private typealias Key = MovieDetailKey
private typealias View = MovieDetailView
private typealias Intent = MovieDetailIntent
private typealias Compositor = MovieDetailCompositor
private typealias Effects = ViceEffects
private typealias ViewState = MovieDetailViewState
