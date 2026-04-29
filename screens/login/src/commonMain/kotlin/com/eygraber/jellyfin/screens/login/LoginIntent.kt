package com.eygraber.jellyfin.screens.login

import com.eygraber.vice.filter.ThrottlingIntent

sealed interface LoginIntent {
  data class LoginClicked(
    val serverUrl: String,
    val username: String,
    val password: String,
  ) : LoginIntent, ThrottlingIntent

  data object BackClicked : LoginIntent, ThrottlingIntent
}
