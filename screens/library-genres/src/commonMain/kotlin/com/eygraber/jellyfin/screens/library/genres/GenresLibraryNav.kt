package com.eygraber.jellyfin.screens.library.genres

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
data class GenresLibraryKey(
  val libraryId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class GenresLibraryNavEntryProvider(
  override val compositor: GenresLibraryCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> GenresLibraryView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface GenresLibraryGraph {
  val navEntryProvider: GenresLibraryNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createGenresLibraryGraph(
      @Provides navigator: GenresLibraryNavigator,
      @Provides key: GenresLibraryKey,
    ): GenresLibraryGraph
  }
}

private typealias Key = GenresLibraryKey
private typealias View = GenresLibraryView
private typealias Intent = GenresLibraryIntent
private typealias Compositor = GenresLibraryCompositor
private typealias Effects = ViceEffects
private typealias ViewState = GenresLibraryViewState
