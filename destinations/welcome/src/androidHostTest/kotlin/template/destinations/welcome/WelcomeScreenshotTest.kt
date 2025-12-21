package template.destinations.welcome

import app.cash.paparazzi.Paparazzi
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import template.test.utils.PaparazziComposeResourcesEffect
import template.test.utils.PaparazziDeviceConfig
import template.ui.material.theme.TemplateEdgeToEdgePreviewTheme

@RunWith(TestParameterInjector::class)
class WelcomeScreenshotTest(
  @Suppress("UnnecessaryAnnotationUseSiteTarget")
  @param:TestParameter
  private val deviceConfig: PaparazziDeviceConfig,
) {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = deviceConfig.config,
  )

  @Test
  fun screenshot() {
    paparazzi.snapshot {
      PaparazziComposeResourcesEffect()

      TemplateEdgeToEdgePreviewTheme(isDarkMode = deviceConfig.isDarkMode) {
        WelcomeView(
          state = WelcomeViewState,
          onIntent = {},
        )
      }
    }
  }
}
