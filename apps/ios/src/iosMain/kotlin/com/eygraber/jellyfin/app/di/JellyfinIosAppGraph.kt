package com.eygraber.jellyfin.app.di

import com.eygraber.jellyfin.apps.shared.JellyfinInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface JellyfinIosAppGraph : JellyfinIosViewControllerGraph.Factory {
  val initializer: JellyfinInitializer

  @DependencyGraph.Factory
  interface Factory {
    fun create(): JellyfinIosAppGraph
  }
}
