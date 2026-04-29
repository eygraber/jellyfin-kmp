package com.eygraber.jellyfin.screens.login.model

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import com.eygraber.jellyfin.screens.login.LoginFieldsState
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

@Inject
class LoginFieldsModel : ViceSource<LoginFieldsState> {
  @Composable
  override fun currentState() = LoginFieldsState(
    serverUrl = rememberTextFieldState(),
    username = rememberTextFieldState(),
    password = rememberTextFieldState(),
  )
}
