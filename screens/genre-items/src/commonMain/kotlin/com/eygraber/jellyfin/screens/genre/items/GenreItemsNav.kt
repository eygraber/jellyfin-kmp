package com.eygraber.jellyfin.screens.genre.items

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
data class GenreItemsKey(
  val libraryId: String,
  val genreName: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class GenreItemsNavEntryProvider(
  override val compositor: GenreItemsCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> GenreItemsView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface GenreItemsGraph {
  val navEntryProvider: GenreItemsNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createGenreItemsGraph(
      @Provides navigator: GenreItemsNavigator,
      @Provides key: GenreItemsKey,
    ): GenreItemsGraph
  }
}

private typealias Key = GenreItemsKey
private typealias View = GenreItemsView
private typealias Intent = GenreItemsIntent
private typealias Compositor = GenreItemsCompositor
private typealias Effects = ViceEffects
private typealias ViewState = GenreItemsViewState
