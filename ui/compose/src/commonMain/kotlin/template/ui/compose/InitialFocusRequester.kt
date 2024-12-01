package template.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester

@Composable
internal expect fun FocusRequester.RequestInitialFocus()

@Composable
fun rememberInitialFocusRequester(): FocusRequester =
  remember { FocusRequester() }.apply {
    RequestInitialFocus()
  }
