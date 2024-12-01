package template.nav.dev

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import kotlinx.coroutines.flow.filter
import template.services.device.sensors.ShakeDetector

@Composable
internal fun DetectShakesEffect(
  shakeDetector: ShakeDetector,
  navController: NavController,
) {
  LaunchedEffect(Unit) {
    shakeDetector
      .detectShakes()
      .filter {
        // don't handle a shake if dev settings is already showing
        runCatching {
          navController.getBackStackEntry<TemplateRoutesDevSettings>()
        }.getOrNull() == null
      }
      .collect {
        navController.navigate(TemplateRoutesDevSettings)
      }
  }
}
