package template.screens.welcome

import com.eygraber.vice.filter.ThrottlingIntent

sealed interface WelcomeIntent {
  data object SignUp : WelcomeIntent, ThrottlingIntent
  data object Login : WelcomeIntent, ThrottlingIntent
}
