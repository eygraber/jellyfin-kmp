package com.eygraber.jellyfin.screens.welcome

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
class WelcomeScreenshotTest(
  @Suppress("UnnecessaryAnnotationUseSiteTarget")
  @param:TestParameter
  private val deviceConfig: PaparazziDeviceConfig,
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
        WelcomeView(
          state = WelcomeViewState,
          onIntent = {},
        )
      }
    }
  }
}
