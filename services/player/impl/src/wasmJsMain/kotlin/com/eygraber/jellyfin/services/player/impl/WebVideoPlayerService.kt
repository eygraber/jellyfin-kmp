package com.eygraber.jellyfin.services.player.impl

import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.services.player.VideoPlayerService
import dev.zacsweers.metro.ContributesBinding

/**
 * Web stub implementation of [VideoPlayerService].
 *
 * HTML5 Video integration will be added in a future issue.
 */
@ContributesBinding(ScreenScope::class)
class WebVideoPlayerService : NoOpVideoPlayerService()
