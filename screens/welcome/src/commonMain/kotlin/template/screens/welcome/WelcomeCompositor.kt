package template.screens.welcome

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
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
