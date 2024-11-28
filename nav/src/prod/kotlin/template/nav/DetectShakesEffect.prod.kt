package template.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import template.services.device.sensors.ShakeDetector

@Composable
internal fun DetectShakesEffect(
  shakeDetector: ShakeDetector,
  navController: NavController,
) {}
