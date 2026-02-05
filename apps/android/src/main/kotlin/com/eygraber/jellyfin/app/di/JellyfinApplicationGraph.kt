package com.eygraber.jellyfin.app.di

import android.app.Application
import com.eygraber.jellyfin.app.di.android.AndroidAppProviders
import com.eygraber.jellyfin.apps.shared.JellyfinInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
interface JellyfinApplicationGraph : AndroidAppProviders, JellyfinActivityGraph.Factory {
  val initializer: JellyfinInitializer

  @DependencyGraph.Factory
  interface Factory {
    fun create(@Provides application: Application): JellyfinApplicationGraph
  }
}
