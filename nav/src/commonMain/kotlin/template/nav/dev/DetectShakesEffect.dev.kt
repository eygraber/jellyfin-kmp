package template.nav.dev

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.filter
import template.screens.dev.settings.DevSettingsKey
import template.services.device.sensors.ShakeDetector

@Composable
internal fun DetectShakesEffect(
  shakeDetector: ShakeDetector,
  backStack: NavBackStack<NavKey>,
) {
  LaunchedEffect(Unit) {
    shakeDetector
      .detectShakes()
      .filter {
        // don't handle a shake if dev settings is already showing
        backStack.lastOrNull() !is DevSettingsKey
      }
      .collect {
        backStack.add(DevSettingsKey)
      }
  }
}
