package com.eygraber.jellyfin.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.HidePassword
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.ShowPassword
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView
import org.jetbrains.compose.resources.stringResource

internal typealias LoginView = ViceView<LoginIntent, LoginViewState>

private val PasswordOutputTransformation = OutputTransformation {
  replace(0, length, "\u2022".repeat(length))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginView(
  state: LoginViewState,
  onIntent: (LoginIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(stringResource(Res.string.login_title)) },
          navigationIcon = {
            IconButton(onClick = { onIntent(LoginIntent.BackClicked) }) {
              Icon(
                imageVector = JellyfinIcons.ArrowBack,
                contentDescription = stringResource(Res.string.login_cd_back),
              )
            }
          },
        )
      },
    ) { contentPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding)
          .padding(horizontal = 16.dp)
          .imePadding()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        val loginClickedIntent = LoginIntent.LoginClicked(
          serverUrl = state.fields.serverUrl.text.toString(),
          username = state.fields.username.text.toString(),
          password = state.fields.password.text.toString(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        ServerUrlField(
          state = state.fields.serverUrl,
          error = state.serverUrlError,
          enabled = !state.isLoading,
        )

        Spacer(modifier = Modifier.height(16.dp))

        UsernameField(
          state = state.fields.username,
          enabled = !state.isLoading,
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
          state = state.fields.password,
          enabled = !state.isLoading,
          onDone = { onIntent(loginClickedIntent) },
        )

        if(state.loginError != null) {
          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = loginErrorMessage(state.loginError),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = { onIntent(loginClickedIntent) },
          modifier = Modifier.fillMaxWidth(),
          enabled = state.isLoginEnabled,
        ) {
          if(state.isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = MaterialTheme.colorScheme.onPrimary,
              strokeWidth = 2.dp,
            )
          }
          else {
            Text(stringResource(Res.string.login_button))
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun ServerUrlField(
  state: TextFieldState,
  error: ServerUrlError?,
  enabled: Boolean,
) {
  OutlinedTextField(
    state = state,
    modifier = Modifier.fillMaxWidth(),
    enabled = enabled,
    label = { Text(stringResource(Res.string.login_server_url_label)) },
    placeholder = { Text(stringResource(Res.string.login_server_url_placeholder)) },
    isError = error != null,
    supportingText = error?.let {
      { Text(serverUrlErrorMessage(it)) }
    },
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Uri,
      imeAction = ImeAction.Next,
    ),
    lineLimits = TextFieldLineLimits.SingleLine,
  )
}

@Composable
private fun UsernameField(
  state: TextFieldState,
  enabled: Boolean,
) {
  OutlinedTextField(
    state = state,
    modifier = Modifier.fillMaxWidth(),
    enabled = enabled,
    label = { Text(stringResource(Res.string.login_username_label)) },
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Text,
      imeAction = ImeAction.Next,
    ),
    lineLimits = TextFieldLineLimits.SingleLine,
  )
}

@Composable
private fun PasswordField(
  state: TextFieldState,
  enabled: Boolean,
  onDone: () -> Unit,
) {
  var passwordVisible by rememberSaveable { mutableStateOf(false) }

  OutlinedTextField(
    state = state,
    modifier = Modifier.fillMaxWidth(),
    enabled = enabled,
    label = { Text(stringResource(Res.string.login_password_label)) },
    outputTransformation = if(passwordVisible) null else PasswordOutputTransformation,
    trailingIcon = {
      IconButton(onClick = { passwordVisible = !passwordVisible }) {
        Icon(
          imageVector = if(passwordVisible) JellyfinIcons.HidePassword else JellyfinIcons.ShowPassword,
          contentDescription = if(passwordVisible) {
            stringResource(Res.string.login_cd_hide_password)
          }
          else {
            stringResource(Res.string.login_cd_show_password)
          },
        )
      }
    },
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Password,
      imeAction = ImeAction.Done,
    ),
    onKeyboardAction = { onDone() },
    lineLimits = TextFieldLineLimits.SingleLine,
  )
}

@Composable
private fun serverUrlErrorMessage(error: ServerUrlError): String = when(error) {
  ServerUrlError.Empty -> stringResource(Res.string.login_error_server_url_empty)
  ServerUrlError.InvalidFormat -> stringResource(Res.string.login_error_server_url_invalid)
  ServerUrlError.InsecureProtocol -> stringResource(Res.string.login_error_server_url_insecure)
}

@Composable
private fun loginErrorMessage(error: LoginError): String = when(error) {
  LoginError.InvalidCredentials -> stringResource(Res.string.login_error_invalid_credentials)
  LoginError.ServerUnreachable -> stringResource(Res.string.login_error_server_unreachable)
  is LoginError.Unknown -> error.message ?: stringResource(Res.string.login_error_unknown)
}

@PreviewJellyfinScreen
@Composable
private fun LoginPreview() {
  JellyfinPreviewTheme {
    LoginView(
      state = LoginViewState(
        fields = LoginFieldsState(
          serverUrl = TextFieldState("https://jellyfin.example.com"),
          username = TextFieldState("admin"),
          password = TextFieldState("password"),
        ),
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun LoginErrorPreview() {
  JellyfinPreviewTheme {
    LoginView(
      state = LoginViewState(
        fields = LoginFieldsState(
          serverUrl = TextFieldState("bad-url"),
          username = TextFieldState("admin"),
          password = TextFieldState("password"),
        ),
        serverUrlError = ServerUrlError.InvalidFormat,
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun LoginLoadingPreview() {
  JellyfinPreviewTheme {
    LoginView(
      state = LoginViewState(
        fields = LoginFieldsState(
          serverUrl = TextFieldState("https://jellyfin.example.com"),
          username = TextFieldState("admin"),
          password = TextFieldState("password"),
        ),
        isLoading = true,
      ),
      onIntent = {},
    )
  }
}
