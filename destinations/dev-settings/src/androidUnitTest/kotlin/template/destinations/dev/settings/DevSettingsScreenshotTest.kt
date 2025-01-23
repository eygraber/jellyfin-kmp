package template.destinations.dev.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import template.ui.compose.WithDensity
import template.ui.material.theme.TemplateEdgeToEdgeModalBottomSheetPreviewTheme

@RunWith(TestParameterInjector::class)
class DevSettingsScreenshotTest {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = DeviceConfig.PIXEL,
  )

  @TestParameter
  private var isDarkMode: Boolean = false

  @TestParameter("1", "2")
  private var fontScale: Float = 1F

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun screenshot(
    @TestParameter
    skipPartiallyExpanded: Boolean,
  ) {
    paparazzi.snapshot {
      TemplateEdgeToEdgeModalBottomSheetPreviewTheme(
        isDarkMode = isDarkMode,
        initialValue = when {
          skipPartiallyExpanded -> SheetValue.Expanded
          else -> SheetValue.PartiallyExpanded
        },
        skipPartiallyExpanded = skipPartiallyExpanded,
      ) {
        WithDensity(fontScale = fontScale) {
          DevSettingsView(
            state = DevSettingsViewState,
            onIntent = {},
          )
        }
      }
    }
  }
}
