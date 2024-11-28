package template.services.device.sensors

import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import template.di.scopes.ActivityScope
import com.squareup.seismic.ShakeDetector as SeismicShakeDetector

@Inject
@ContributesBinding(ActivityScope::class)
class AndroidShakeDetector(
  private val sensorManager: SensorManager,
) : ShakeDetector {
  override fun detectShakes(): Flow<Unit> = callbackFlow {
    val shakeDetector = SeismicShakeDetector {
      trySend(Unit)
    }
    shakeDetector.start(sensorManager, SensorManager.SENSOR_DELAY_NORMAL)

    awaitClose {
      shakeDetector.stop()
    }
  }
}
