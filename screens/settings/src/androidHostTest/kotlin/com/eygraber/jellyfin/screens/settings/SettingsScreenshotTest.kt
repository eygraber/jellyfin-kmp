package com.eygraber.jellyfin.screens.settings

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
class SettingsScreenshotTest(
  @Suppress("UnnecessaryAnnotationUseSiteTarget")
  @param:TestParameter
  private val deviceConfig: PaparazziDeviceConfig,
  @Suppress("UnnecessaryAnnotationUseSiteTarget")
  @param:TestParameter
  private val variant: SettingsScreenshotVariant,
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
        SettingsView(
          state = variant.state,
          onIntent = {},
        )
      }
    }
  }
}

enum class SettingsScreenshotVariant(val state: SettingsViewState) {
  Authenticated(
    SettingsViewState(
      userInfo = SettingsUserInfo(
        username = "alice",
        serverName = "Home Server",
        serverUrl = "https://jellyfin.example.com",
      ),
    ),
  ),
  NoUser(
    SettingsViewState(),
  ),
  SignOutDialog(
    SettingsViewState(
      userInfo = SettingsUserInfo(
        username = "alice",
        serverName = "Home Server",
        serverUrl = "https://jellyfin.example.com",
      ),
      isSignOutDialogVisible = true,
    ),
  ),
  SigningOut(
    SettingsViewState(
      userInfo = SettingsUserInfo(
        username = "alice",
        serverName = "Home Server",
        serverUrl = "https://jellyfin.example.com",
      ),
      isSignOutDialogVisible = true,
      isSigningOut = true,
    ),
  ),
}
