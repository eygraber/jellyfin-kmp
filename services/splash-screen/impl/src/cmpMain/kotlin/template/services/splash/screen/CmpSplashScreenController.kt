package template.services.splash.screen

import kotlinx.coroutines.delay
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.SessionScope
import kotlin.time.Duration.Companion.milliseconds

@Inject
@SingleIn(SessionScope::class)
@ContributesBinding(SessionScope::class)
class CmpSplashScreenController : SplashScreenController {
  override fun init(isAppRestoring: Boolean) {}

  override suspend fun awaitSystemSplashRemoved() {
    delay(500.milliseconds)
  }
}
