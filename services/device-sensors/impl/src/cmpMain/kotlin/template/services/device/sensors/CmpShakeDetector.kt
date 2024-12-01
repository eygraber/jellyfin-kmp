package template.services.device.sensors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import template.di.scopes.SessionScope

@Inject
@ContributesBinding(SessionScope::class)
class CmpShakeDetector : ShakeDetector {
  override fun detectShakes(): Flow<Unit> = emptyFlow()
}
