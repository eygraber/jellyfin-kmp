package template.destinations.dev.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.eygraber.vice.ViceView
import template.ui.compose.PreviewTemplateScreen
import template.ui.material.theme.TemplatePreviewTheme

internal typealias DevSettingsView = ViceView<DevSettingsIntent, DevSettingsViewState>

@Suppress("UNUSED_PARAMETER")
@Composable
internal fun DevSettingsView(
  state: DevSettingsViewState,
  onIntent: (DevSettingsIntent) -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize(),
  ) {
    Text("DevSettings")
  }
}

@PreviewTemplateScreen
@Composable
private fun DevSettingsPreview(
  @PreviewParameter(ViewStatePreviewProvider::class)
  state: DevSettingsViewState,
) {
  TemplatePreviewTheme {
    DevSettingsView(
      state = state,
      onIntent = {},
    )
  }
}
