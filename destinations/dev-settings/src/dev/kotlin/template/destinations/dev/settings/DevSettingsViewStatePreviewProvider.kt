@file:Suppress("ktlint:standard:argument-list-wrapping", "ktlint:standard:max-line-length")

package template.destinations.dev.settings

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class DevSettingsViewStatePreviewProvider : PreviewParameterProvider<DevSettingsViewState> {
  override val values = sequenceOf(
    DevSettingsViewState,
  )
}

internal typealias ViewStatePreviewProvider = DevSettingsViewStatePreviewProvider
