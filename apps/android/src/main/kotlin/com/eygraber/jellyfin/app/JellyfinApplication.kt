package com.eygraber.jellyfin.app

import android.app.Application
import com.eygraber.jellyfin.app.di.JellyfinApplicationGraph
import dev.zacsweers.metro.createGraphFactory

class JellyfinApplication : Application() {
  val graph by lazy {
    createGraphFactory<JellyfinApplicationGraph.Factory>().create(
      application = this,
    )
  }
}

internal val Application.jellyfinApplicationGraph get() = (this as JellyfinApplication).graph
