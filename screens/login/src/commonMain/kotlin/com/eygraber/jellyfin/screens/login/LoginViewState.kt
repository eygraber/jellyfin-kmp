package com.eygraber.jellyfin.screens.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable

data class LoginFieldsState(
  val serverUrl: TextFieldState = TextFieldState(),
  val username: TextFieldState = TextFieldState(),
  val password: TextFieldState = TextFieldState(),
)

@Immutable
data class LoginViewState(
  val fields: LoginFieldsState = LoginFieldsState(),
  val serverUrlError: ServerUrlError? = null,
  val loginError: LoginError? = null,
  val isLoading: Boolean = false,
) {
  val isLoginEnabled: Boolean
    get() = fields.serverUrl.text.isNotBlank() &&
      fields.username.text.isNotBlank() &&
      fields.password.text.isNotBlank() &&
      !isLoading
}

@Immutable
enum class ServerUrlError {
  Empty,
  InvalidFormat,
  InsecureProtocol,
}

@Immutable
sealed interface LoginError {
  data object InvalidCredentials : LoginError
  data object ServerUnreachable : LoginError
  data class Unknown(val message: String?) : LoginError
}
