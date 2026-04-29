package com.eygraber.jellyfin.screens.settings

sealed interface SettingsIntent {
  data object BackClicked : SettingsIntent
  data class CategoryClicked(val category: SettingsCategory) : SettingsIntent
  data object SignOutClicked : SettingsIntent
  data object SignOutConfirmed : SettingsIntent
  data object SignOutDismissed : SettingsIntent
}
