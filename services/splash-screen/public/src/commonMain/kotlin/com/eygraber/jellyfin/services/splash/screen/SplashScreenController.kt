package com.eygraber.jellyfin.services.splash.screen

interface SplashScreenController {
  fun init(isAppRestoring: Boolean)

  suspend fun awaitSystemSplashRemoved()
}
