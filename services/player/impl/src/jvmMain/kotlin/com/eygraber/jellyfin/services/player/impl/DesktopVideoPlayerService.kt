package com.eygraber.jellyfin.services.player.impl

import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.services.player.VideoPlayerService
import dev.zacsweers.metro.ContributesBinding

/**
 * Desktop stub implementation of [VideoPlayerService].
 *
 * VLC/JavaFX integration will be added in a future issue.
 */
@ContributesBinding(ScreenScope::class)
class DesktopVideoPlayerService : NoOpVideoPlayerService()
