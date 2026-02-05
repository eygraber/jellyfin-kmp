package com.eygraber.jellyfin.ui.material.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

enum class SystemDarkModeOverride {
  None,
  Dark,
  Light,
  ;

  companion object {
    private val overrides = mutableStateListOf<SystemDarkModeOverride>()

    fun pushOverride(override: SystemDarkModeOverride) {
      overrides.add(override)
    }

    fun popOverride() {
      overrides.removeFirstOrNull()
    }

    @Composable
    fun rememberState(): SystemDarkModeOverride {
      val overrideList = remember { overrides }
      return overrideList.lastOrNull() ?: None
    }
  }
}
