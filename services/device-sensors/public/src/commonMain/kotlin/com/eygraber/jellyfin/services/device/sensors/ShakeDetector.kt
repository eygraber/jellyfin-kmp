package com.eygraber.jellyfin.services.device.sensors

import kotlinx.coroutines.flow.Flow

interface ShakeDetector {
  fun detectShakes(): Flow<Unit>
}
