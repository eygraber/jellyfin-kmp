package com.eygraber.jellyfin.screens.settings

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsViewState(
  val userInfo: SettingsUserInfo? = null,
  val categories: List<SettingsCategory> = SettingsCategory.entries,
  val isSignOutDialogVisible: Boolean = false,
  val isSigningOut: Boolean = false,
)

@Immutable
data class SettingsUserInfo(
  val username: String,
  val serverName: String,
  val serverUrl: String,
)
