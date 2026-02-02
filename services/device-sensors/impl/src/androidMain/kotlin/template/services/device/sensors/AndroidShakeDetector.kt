package template.services.device.sensors

import android.hardware.SensorManager
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import template.di.scopes.SessionScope
import com.squareup.seismic.ShakeDetector as SeismicShakeDetector

@ContributesBinding(SessionScope::class)
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
