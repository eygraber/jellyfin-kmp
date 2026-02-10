package com.eygraber.jellyfin.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.auth.AuthRepository
import com.eygraber.jellyfin.domain.server.ServerManager
import com.eygraber.jellyfin.domain.session.SessionManager
import com.eygraber.jellyfin.domain.validators.ServerUrlValidator
import com.eygraber.jellyfin.screens.login.model.LoginFieldsModel
import com.eygraber.jellyfin.ui.material.text.UpdateEffect
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class LoginCompositor(
  private val serverUrlValidator: ServerUrlValidator,
  private val serverManager: ServerManager,
  private val authRepository: AuthRepository,
  private val sessionManager: SessionManager,
  private val navigator: LoginNavigator,
  private val fieldsModel: LoginFieldsModel,
) : ViceCompositor<LoginIntent, LoginViewState> {
  private var serverUrlError by mutableStateOf<ServerUrlError?>(null)
  private var loginError by mutableStateOf<LoginError?>(null)
  private var isLoading by mutableStateOf(false)

  @Composable
  override fun composite(): LoginViewState {
    val fields = fieldsModel.currentState()

    fields.serverUrl.UpdateEffect { _ ->
      serverUrlError = null
      loginError = null
    }

    fields.username.UpdateEffect { _ ->
      loginError = null
    }

    fields.password.UpdateEffect { _ ->
      loginError = null
    }

    return LoginViewState(
      fields = fields,
      serverUrlError = serverUrlError,
      loginError = loginError,
      isLoading = isLoading,
    )
  }

  override suspend fun onIntent(intent: LoginIntent) {
    when(intent) {
      is LoginIntent.LoginClicked -> performLogin(intent)
      LoginIntent.BackClicked -> navigator.navigateBack()
    }
  }

  private suspend fun performLogin(intent: LoginIntent.LoginClicked) {
    val validationResult = serverUrlValidator.validate(intent.serverUrl)
    when(validationResult) {
      ServerUrlValidator.Result.Empty -> {
        serverUrlError = ServerUrlError.Empty
        return
      }

      ServerUrlValidator.Result.InvalidFormat -> {
        serverUrlError = ServerUrlError.InvalidFormat
        return
      }

      ServerUrlValidator.Result.InsecureProtocol -> {
        serverUrlError = ServerUrlError.InsecureProtocol
        return
      }

      ServerUrlValidator.Result.Valid -> serverUrlError = null
    }

    val normalizedUrl = serverUrlValidator.normalize(intent.serverUrl) ?: run {
      serverUrlError = ServerUrlError.InvalidFormat
      return
    }

    isLoading = true
    loginError = null

    val serverResult = serverManager.addServer(normalizedUrl)
    if(!serverResult.isSuccess()) {
      isLoading = false
      loginError = LoginError.ServerUnreachable
      return
    }

    val server = serverResult.value

    val authResult = authRepository.login(
      serverId = server.id,
      serverUrl = server.url,
      username = intent.username.trim(),
      password = intent.password,
    )

    if(authResult.isSuccess()) {
      val session = authResult.value
      sessionManager.onLoginSuccess(
        serverId = server.id,
        serverUrl = server.url,
        accessToken = session.accessToken,
        userId = session.userId,
      )
      isLoading = false
      navigator.navigateToHome()
    }
    else {
      isLoading = false
      loginError = LoginError.InvalidCredentials
    }
  }
}
