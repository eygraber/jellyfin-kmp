package com.eygraber.jellyfin.screens.dev.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import app.cash.paparazzi.Paparazzi
import com.eygraber.jellyfin.test.utils.PaparazziDeviceConfig
import com.eygraber.jellyfin.ui.material.theme.JellyfinEdgeToEdgeModalBottomSheetPreviewTheme
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class DevSettingsScreenshotTest(
  @Suppress("UnnecessaryAnnotationUseSiteTarget")
  @param:TestParameter
  private val deviceConfig: PaparazziDeviceConfig,
) {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = deviceConfig.config,
  )

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun screenshot(
    @TestParameter
    skipPartiallyExpanded: Boolean,
  ) {
    paparazzi.snapshot {
      JellyfinEdgeToEdgeModalBottomSheetPreviewTheme(
        isDarkMode = deviceConfig.isDarkMode,
        initialValue = when {
          skipPartiallyExpanded -> SheetValue.Expanded
          else -> SheetValue.PartiallyExpanded
        },
        skipPartiallyExpanded = skipPartiallyExpanded,
      ) {
        DevSettingsView(
          state = DevSettingsViewState,
          onIntent = {},
        )
      }
    }
  }
}
