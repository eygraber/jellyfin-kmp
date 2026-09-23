package com.eygraber.jellyfin.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.focus.FocusRequester

@Composable
internal actual fun FocusRequester.RequestInitialFocus() {
  SideEffect(this) {
    requestFocus()
  }
}
