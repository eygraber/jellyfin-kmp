package com.eygraber.jellyfin.screens.root

import com.eygraber.jellyfin.domain.session.SessionManager
import com.eygraber.jellyfin.domain.session.SessionState
import com.eygraber.jellyfin.services.splash.screen.SplashScreenController
import com.eygraber.vice.ViceEffects
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Inject
class RootEffects(
  private val splashScreenController: SplashScreenController,
  private val sessionManager: SessionManager,
  private val navigator: RootNavigator,
  private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViceEffects {
  override fun CoroutineScope.runEffects() {
    launch(mainDispatcher) {
      splashScreenController.awaitSystemSplashRemoved()

      when(sessionManager.restoreSession()) {
        is SessionState.Authenticated -> navigator.navigateToHome()
        is SessionState.Loading,
        is SessionState.NoSession,
        is SessionState.SessionExpired,
        -> navigator.navigateToOnboarding()
      }
    }
  }
}
