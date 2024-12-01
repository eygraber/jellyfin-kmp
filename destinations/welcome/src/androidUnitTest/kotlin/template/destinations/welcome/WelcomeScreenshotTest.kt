package template.destinations.welcome

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import template.ui.compose.WithDensity
import template.ui.material.theme.TemplateEdgeToEdgePreviewTheme

@RunWith(TestParameterInjector::class)
class WelcomeScreenshotTest {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = DeviceConfig.PIXEL,
  )

  @TestParameter
  private var isDarkMode: Boolean = false

  @TestParameter("1", "2")
  private var fontScale: Float = 1F

  @Test
  fun screenshot() {
    paparazzi.snapshot {
      // Paparazzi purposely doesn't enable InspectionMode
      // https://github.com/cashapp/paparazzi/issues/508#issuecomment-1693819132
      CompositionLocalProvider(LocalInspectionMode provides true) {
        TemplateEdgeToEdgePreviewTheme(isDarkMode = isDarkMode) {
          WithDensity(fontScale = fontScale) {
            WelcomeView(
              state = WelcomeViewState,
              onIntent = {},
            )
          }
        }
      }
    }
  }
}
