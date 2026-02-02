package template.screens.root

import com.eygraber.vice.ViceEffects
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import template.services.splash.screen.SplashScreenController

@Inject
class RootEffects(
  private val splashScreenController: SplashScreenController,
  private val navigator: RootNavigator,
  private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViceEffects {
  override fun CoroutineScope.runEffects() {
    launch(mainDispatcher) {
      splashScreenController.awaitSystemSplashRemoved()
      navigator.navigateToOnboarding()
    }
  }
}
