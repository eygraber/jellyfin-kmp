package com.eygraber.jellyfin.nav

import com.eygraber.jellyfin.di.scopes.NavScope
import com.eygraber.jellyfin.di.scopes.SessionScope
import com.eygraber.jellyfin.services.device.sensors.ShakeDetector
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@GraphExtension(NavScope::class)
interface JellyfinNavGraph {
  val shakeDetector: ShakeDetector
  val shortcutManager: NavShortcutManager

  @ContributesTo(SessionScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createJellyfinNavGraph(): JellyfinNavGraph
  }
}
