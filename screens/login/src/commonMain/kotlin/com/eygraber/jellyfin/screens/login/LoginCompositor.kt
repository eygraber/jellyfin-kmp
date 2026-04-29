package com.eygraber.jellyfin.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.successOrNull
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

  internal val serverUrlErrorForTest: ServerUrlError? get() = serverUrlError
  internal val loginErrorForTest: LoginError? get() = loginError
  internal val isLoadingForTest: Boolean get() = isLoading

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
    val urlError = validateServerUrl(intent.serverUrl)
    serverUrlError = urlError
    if(urlError != null) return

    val normalizedUrl = serverUrlValidator.normalize(intent.serverUrl)
    if(normalizedUrl == null) {
      serverUrlError = ServerUrlError.InvalidFormat
      return
    }

    isLoading = true
    loginError = null

    loginError = authenticate(
      normalizedUrl = normalizedUrl,
      username = intent.username.trim(),
      password = intent.password,
    )
    isLoading = false
  }

  private fun validateServerUrl(serverUrl: String): ServerUrlError? =
    when(serverUrlValidator.validate(serverUrl)) {
      ServerUrlValidator.Result.Empty -> ServerUrlError.Empty
      ServerUrlValidator.Result.InvalidFormat -> ServerUrlError.InvalidFormat
      ServerUrlValidator.Result.InsecureProtocol -> ServerUrlError.InsecureProtocol
      ServerUrlValidator.Result.Valid -> null
    }

  private suspend fun authenticate(
    normalizedUrl: String,
    username: String,
    password: String,
  ): LoginError? {
    val server = serverManager.addServer(normalizedUrl).successOrNull
      ?: return LoginError.ServerUnreachable

    val session = authRepository.login(
      serverId = server.id,
      serverUrl = server.url,
      username = username,
      password = password,
    ).successOrNull ?: return LoginError.InvalidCredentials

    sessionManager.onLoginSuccess(
      serverId = server.id,
      serverUrl = server.url,
      accessToken = session.accessToken,
      userId = session.userId,
    )
    navigator.navigateToHome()
    return null
  }
}
