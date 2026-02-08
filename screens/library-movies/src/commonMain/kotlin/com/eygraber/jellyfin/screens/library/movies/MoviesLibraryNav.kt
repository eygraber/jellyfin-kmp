package com.eygraber.jellyfin.screens.library.movies

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
data class MoviesLibraryKey(
  val libraryId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class MoviesLibraryNavEntryProvider(
  override val compositor: MoviesLibraryCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> MoviesLibraryView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface MoviesLibraryGraph {
  val navEntryProvider: MoviesLibraryNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createMoviesLibraryGraph(
      @Provides navigator: MoviesLibraryNavigator,
      @Provides key: MoviesLibraryKey,
    ): MoviesLibraryGraph
  }
}

private typealias Key = MoviesLibraryKey
private typealias View = MoviesLibraryView
private typealias Intent = MoviesLibraryIntent
private typealias Compositor = MoviesLibraryCompositor
private typealias Effects = ViceEffects
private typealias ViewState = MoviesLibraryViewState
