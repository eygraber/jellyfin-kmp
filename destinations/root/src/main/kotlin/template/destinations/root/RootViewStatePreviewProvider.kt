@file:Suppress("ktlint:standard:argument-list-wrapping", "ktlint:standard:max-line-length")

package template.destinations.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class RootViewStatePreviewProvider : PreviewParameterProvider<RootViewState> {
  override val values = sequenceOf(
    RootViewState,
  )
}

internal typealias ViewStatePreviewProvider = RootViewStatePreviewProvider
