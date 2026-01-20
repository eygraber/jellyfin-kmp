package template.ui.compose

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

data class NamedPreviewParameter<T>(
  val name: String,
  val value: T,
)

abstract class NamedPreviewParameterProvider<T> : PreviewParameterProvider<NamedPreviewParameter<T>> {
  private lateinit var valueNames: List<String>

  infix fun String.to(value: T) = NamedPreviewParameter(this, value)

  override fun getDisplayName(index: Int): String {
    if(!::valueNames.isInitialized) {
      valueNames = values.map { it.name }.toList()
    }

    return "$index - ${valueNames[index]}"
  }
}
