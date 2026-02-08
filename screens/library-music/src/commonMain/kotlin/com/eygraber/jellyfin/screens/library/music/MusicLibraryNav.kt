package com.eygraber.jellyfin.screens.library.music

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
data class MusicLibraryKey(
  val libraryId: String,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class MusicLibraryNavEntryProvider(
  override val compositor: MusicLibraryCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> MusicLibraryView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface MusicLibraryGraph {
  val navEntryProvider: MusicLibraryNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createMusicLibraryGraph(
      @Provides navigator: MusicLibraryNavigator,
      @Provides key: MusicLibraryKey,
    ): MusicLibraryGraph
  }
}

private typealias Key = MusicLibraryKey
private typealias View = MusicLibraryView
private typealias Intent = MusicLibraryIntent
private typealias Compositor = MusicLibraryCompositor
private typealias Effects = ViceEffects
private typealias ViewState = MusicLibraryViewState
