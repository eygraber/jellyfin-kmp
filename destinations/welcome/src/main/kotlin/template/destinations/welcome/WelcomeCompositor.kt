package template.destinations.welcome

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.DestinationScope

@Inject
@SingleIn(DestinationScope::class)
class WelcomeCompositor(
  private val navigator: WelcomeNavigator,
) : ViceCompositor<WelcomeIntent, WelcomeViewState> {
  @Composable
  override fun composite() = WelcomeViewState

  override suspend fun onIntent(intent: WelcomeIntent) {
    when(intent) {
      WelcomeIntent.Login -> navigator.navigateToLogin()
      WelcomeIntent.SignUp -> navigator.navigateToSignUp()
    }
  }
}
