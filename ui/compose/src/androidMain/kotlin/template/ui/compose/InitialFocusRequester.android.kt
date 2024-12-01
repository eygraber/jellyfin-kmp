package template.ui.compose

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalView

@Composable
internal actual fun FocusRequester.RequestInitialFocus() {
  val v = LocalView.current
  LaunchedEffect(this) {
    if(Build.FINGERPRINT == "robolectric") {
      // post to workaround https://github.com/robolectric/robolectric/issues/9703
      v.post {
        requestFocus()
      }
    }
    else {
      requestFocus()
    }
  }
}
