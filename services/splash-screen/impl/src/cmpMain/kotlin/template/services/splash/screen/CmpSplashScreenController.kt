package template.services.splash.screen

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.delay
import template.di.scopes.SessionScope
import kotlin.time.Duration.Companion.milliseconds

@SingleIn(SessionScope::class)
@ContributesBinding(SessionScope::class)
class CmpSplashScreenController : SplashScreenController {
  override fun init(isAppRestoring: Boolean) {}

  override suspend fun awaitSystemSplashRemoved() {
    delay(500.milliseconds)
  }
}
