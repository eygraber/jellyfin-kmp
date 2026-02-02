package template.services.device.sensors

import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import template.di.scopes.SessionScope

@ContributesBinding(SessionScope::class)
class CmpShakeDetector : ShakeDetector {
  override fun detectShakes(): Flow<Unit> = emptyFlow()
}
