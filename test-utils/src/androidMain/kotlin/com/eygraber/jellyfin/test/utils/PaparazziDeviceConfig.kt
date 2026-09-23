package com.eygraber.jellyfin.test.utils

import app.cash.paparazzi.DeviceConfig
import com.android.resources.NightMode

enum class PaparazziDeviceConfig(val config: DeviceConfig) {
  LightMode(
    DeviceConfig.PIXEL.copy(
      nightMode = NightMode.NOTNIGHT,
      fontScale = 1F,
    ),
  ),
  LightModeLargeFontScale(
    DeviceConfig.PIXEL.copy(
      nightMode = NightMode.NOTNIGHT,
      fontScale = 2F,
    ),
  ),
  DarkMode(
    DeviceConfig.PIXEL.copy(
      nightMode = NightMode.NIGHT,
      fontScale = 1F,
    ),
  ),
  DarkModeLargeFontScale(
    DeviceConfig.PIXEL.copy(
      nightMode = NightMode.NIGHT,
      fontScale = 2F,
    ),
  ),
  ;

  val isDarkMode
    get() = when(this) {
      LightMode -> false
      LightModeLargeFontScale -> false
      DarkMode -> true
      DarkModeLargeFontScale -> true
    }
}
