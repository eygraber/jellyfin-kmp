package com.eygraber.jellyfin.app.init

import android.content.Context
import androidx.startup.Initializer
import com.eygraber.jellyfin.app.JellyfinApplication

class JellyfinAndroidXInitializer : Initializer<Unit> {
  override fun create(context: Context) {
    (context as JellyfinApplication).graph.initializer.initialize()
  }

  override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
