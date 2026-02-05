package com.eygraber.jellyfin.app.di

import com.eygraber.jellyfin.apps.shared.JellyfinInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface JellyfinDesktopAppGraph : JellyfinDesktopSessionGraph.Factory {
  val initializer: JellyfinInitializer

  @DependencyGraph.Factory
  interface Factory {
    fun create(): JellyfinDesktopAppGraph
  }
}
