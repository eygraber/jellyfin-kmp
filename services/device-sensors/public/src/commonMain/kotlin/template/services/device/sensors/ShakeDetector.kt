package template.services.device.sensors

import kotlinx.coroutines.flow.Flow

interface ShakeDetector {
  fun detectShakes(): Flow<Unit>
}
