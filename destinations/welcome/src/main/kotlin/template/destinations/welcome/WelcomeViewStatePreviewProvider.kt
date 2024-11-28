@file:Suppress("ktlint:standard:argument-list-wrapping", "ktlint:standard:max-line-length")

package template.destinations.welcome

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class WelcomeViewStatePreviewProvider : PreviewParameterProvider<WelcomeViewState> {
  override val values = sequenceOf(
    WelcomeViewState,
  )
}

internal typealias ViewStatePreviewProvider = WelcomeViewStatePreviewProvider
