package template.destinations.welcome

import com.eygraber.vice.ViceEffects
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.DestinationScope

@Inject
@SingleIn(DestinationScope::class)
class WelcomeEffects : ViceEffects {
  override fun CoroutineScope.runEffects() {}
}
