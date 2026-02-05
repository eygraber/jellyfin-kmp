package com.eygraber.jellyfin.apps.shared

import com.juul.khronicle.ConsoleLogger
import com.juul.khronicle.Log

internal actual fun JellyfinInitializer.initializeEnvironment() {
  if(BuildKonfig.isDev) {
    Log.dispatcher.install(ConsoleLogger)
  }
}
