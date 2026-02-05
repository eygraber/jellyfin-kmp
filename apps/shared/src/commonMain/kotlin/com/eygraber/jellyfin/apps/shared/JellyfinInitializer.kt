package com.eygraber.jellyfin.apps.shared

import dev.zacsweers.metro.Inject

internal expect fun JellyfinInitializer.initializeEnvironment()

@Inject
class JellyfinInitializer {
  fun initialize() {
    initializeEnvironment()
  }
}
