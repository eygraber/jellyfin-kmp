package com.eygraber.jellyfin.screens.collection.items

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
data class CollectionItemsKey(
  val collectionId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class CollectionItemsNavEntryProvider(
  override val compositor: CollectionItemsCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> CollectionItemsView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface CollectionItemsGraph {
  val navEntryProvider: CollectionItemsNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createCollectionItemsGraph(
      @Provides navigator: CollectionItemsNavigator,
      @Provides key: CollectionItemsKey,
    ): CollectionItemsGraph
  }
}

private typealias Key = CollectionItemsKey
private typealias View = CollectionItemsView
private typealias Intent = CollectionItemsIntent
private typealias Compositor = CollectionItemsCompositor
private typealias Effects = ViceEffects
private typealias ViewState = CollectionItemsViewState
