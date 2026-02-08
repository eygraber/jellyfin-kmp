package com.eygraber.jellyfin.screens.music.artist.albums

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
data class ArtistAlbumsKey(
  val artistId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class ArtistAlbumsNavEntryProvider(
  override val compositor: ArtistAlbumsCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> ArtistAlbumsView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface ArtistAlbumsGraph {
  val navEntryProvider: ArtistAlbumsNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createArtistAlbumsGraph(
      @Provides navigator: ArtistAlbumsNavigator,
      @Provides key: ArtistAlbumsKey,
    ): ArtistAlbumsGraph
  }
}

private typealias Key = ArtistAlbumsKey
private typealias View = ArtistAlbumsView
private typealias Intent = ArtistAlbumsIntent
private typealias Compositor = ArtistAlbumsCompositor
private typealias Effects = ViceEffects
private typealias ViewState = ArtistAlbumsViewState
