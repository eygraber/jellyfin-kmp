package com.eygraber.jellyfin.services.device.sensors

import com.eygraber.jellyfin.di.scopes.SessionScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@ContributesBinding(SessionScope::class)
class CmpShakeDetector : ShakeDetector {
  override fun detectShakes(): Flow<Unit> = emptyFlow()
}
