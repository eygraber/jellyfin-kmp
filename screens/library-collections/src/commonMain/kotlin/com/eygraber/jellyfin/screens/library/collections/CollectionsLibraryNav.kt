package com.eygraber.jellyfin.screens.library.collections

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
data class CollectionsLibraryKey(
  val libraryId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class CollectionsLibraryNavEntryProvider(
  override val compositor: CollectionsLibraryCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> CollectionsLibraryView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface CollectionsLibraryGraph {
  val navEntryProvider: CollectionsLibraryNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createCollectionsLibraryGraph(
      @Provides navigator: CollectionsLibraryNavigator,
      @Provides key: CollectionsLibraryKey,
    ): CollectionsLibraryGraph
  }
}

private typealias Key = CollectionsLibraryKey
private typealias View = CollectionsLibraryView
private typealias Intent = CollectionsLibraryIntent
private typealias Compositor = CollectionsLibraryCompositor
private typealias Effects = ViceEffects
private typealias ViewState = CollectionsLibraryViewState
