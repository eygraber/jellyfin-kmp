package com.eygraber.jellyfin.screens.music.album.tracks

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
data class AlbumTracksKey(
  val albumId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class AlbumTracksNavEntryProvider(
  override val compositor: AlbumTracksCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> AlbumTracksView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface AlbumTracksGraph {
  val navEntryProvider: AlbumTracksNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createAlbumTracksGraph(
      @Provides navigator: AlbumTracksNavigator,
      @Provides key: AlbumTracksKey,
    ): AlbumTracksGraph
  }
}

private typealias Key = AlbumTracksKey
private typealias View = AlbumTracksView
private typealias Intent = AlbumTracksIntent
private typealias Compositor = AlbumTracksCompositor
private typealias Effects = ViceEffects
private typealias ViewState = AlbumTracksViewState
