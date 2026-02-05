package com.eygraber.jellyfin.services.splash.screen

import com.eygraber.jellyfin.di.scopes.SessionScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@SingleIn(SessionScope::class)
@ContributesBinding(SessionScope::class)
class CmpSplashScreenController : SplashScreenController {
  override fun init(isAppRestoring: Boolean) {}

  override suspend fun awaitSystemSplashRemoved() {
    delay(500.milliseconds)
  }
}
