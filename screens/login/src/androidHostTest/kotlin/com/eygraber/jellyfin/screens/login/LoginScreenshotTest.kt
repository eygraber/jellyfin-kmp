package com.eygraber.jellyfin.screens.login

import androidx.compose.foundation.text.input.TextFieldState
import app.cash.paparazzi.Paparazzi
import com.eygraber.jellyfin.test.utils.PaparazziComposeResourcesEffect
import com.eygraber.jellyfin.test.utils.PaparazziDeviceConfig
import com.eygraber.jellyfin.ui.material.theme.JellyfinEdgeToEdgePreviewTheme
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class LoginScreenshotTest(
  @Suppress("UnnecessaryAnnotationUseSiteTarget")
  @param:TestParameter
  private val deviceConfig: PaparazziDeviceConfig,
  @Suppress("UnnecessaryAnnotationUseSiteTarget")
  @param:TestParameter
  private val variant: LoginScreenshotVariant,
) {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = deviceConfig.config,
  )

  @Test
  fun screenshot() {
    paparazzi.snapshot {
      PaparazziComposeResourcesEffect()

      JellyfinEdgeToEdgePreviewTheme(isDarkMode = deviceConfig.isDarkMode) {
        LoginView(
          state = variant.state,
          onIntent = {},
        )
      }
    }
  }
}

enum class LoginScreenshotVariant(val state: LoginViewState) {
  Default(
    LoginViewState(
      fields = LoginFieldsState(
        serverUrl = TextFieldState("https://jellyfin.example.com"),
        username = TextFieldState("admin"),
        password = TextFieldState("password"),
      ),
    ),
  ),
  InvalidUrl(
    LoginViewState(
      fields = LoginFieldsState(
        serverUrl = TextFieldState("bad-url"),
        username = TextFieldState("admin"),
        password = TextFieldState("password"),
      ),
      serverUrlError = ServerUrlError.InvalidFormat,
    ),
  ),
  InvalidCredentials(
    LoginViewState(
      fields = LoginFieldsState(
        serverUrl = TextFieldState("https://jellyfin.example.com"),
        username = TextFieldState("admin"),
        password = TextFieldState("password"),
      ),
      loginError = LoginError.InvalidCredentials,
    ),
  ),
  Loading(
    LoginViewState(
      fields = LoginFieldsState(
        serverUrl = TextFieldState("https://jellyfin.example.com"),
        username = TextFieldState("admin"),
        password = TextFieldState("password"),
      ),
      isLoading = true,
    ),
  ),
}
