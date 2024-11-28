package template.destinations.root

import com.eygraber.vice.ViceEffects
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.DestinationScope
import template.services.splash.screen.SplashScreenController

@Inject
@SingleIn(DestinationScope::class)
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
