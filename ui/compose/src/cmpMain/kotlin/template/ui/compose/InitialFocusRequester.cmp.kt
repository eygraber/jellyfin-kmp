package template.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester

@Composable
internal actual fun FocusRequester.RequestInitialFocus() {
  LaunchedEffect(this) {
    requestFocus()
  }
}
