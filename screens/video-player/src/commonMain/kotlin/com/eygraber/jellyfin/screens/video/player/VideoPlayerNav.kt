package com.eygraber.jellyfin.screens.video.player

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
data class VideoPlayerKey(
  val itemId: String,
  val itemName: String? = null,
  val startPositionMs: Long = 0L,
) : NavKey

@Inject
@SingleIn(ScreenScope::class)
class VideoPlayerNavEntryProvider(
  override val compositor: VideoPlayerCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> VideoPlayerView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface VideoPlayerGraph {
  val navEntryProvider: VideoPlayerNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createVideoPlayerGraph(
      @Provides navigator: VideoPlayerNavigator,
      @Provides key: VideoPlayerKey,
    ): VideoPlayerGraph
  }
}

private typealias Key = VideoPlayerKey
private typealias View = VideoPlayerView
private typealias Intent = VideoPlayerIntent
private typealias Compositor = VideoPlayerCompositor
private typealias Effects = ViceEffects
private typealias ViewState = VideoPlayerViewState
