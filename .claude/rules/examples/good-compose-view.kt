// Exemplar Compose View following all project conventions
// See .claude/rules/compose.md for complete rules

package com.com.eygraber.jellyfin.screens.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter

// Top-level View: Only accepts state and onIntent
@Composable
internal fun ExampleView(
  state: ExampleViewState,
  onIntent: (ExampleIntent) -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
  ) { contentPadding ->
    ExampleContent(
      onIncrementClick = { onIntent(ExampleIntent.IncrementClick) }, // Specific callback
      onItemClick = { id -> onIntent(ExampleIntent.ItemClick(id)) }, // Specific callback
      title = state.title,
      count = state.count,
      modifier = Modifier.padding(contentPadding),
    )
  }
}

// Child composable: private visibility, specific callbacks, accepts modifier
@Composable
private fun ExampleContent(
  onIncrementClick: () -> Unit,  // Required lambda first
  onItemClick: (String) -> Unit,  // Required lambda second
  title: String,                   // Required params next
  count: Int,
  modifier: Modifier = Modifier,   // Modifier last
) {
  Column(modifier = modifier) { // Single root emitter
    Text(text = title) // No modifier reuse
    Text(text = "Count: $count")
    Button(onClick = onIncrementClick) {
      Text("Increment")
    }
  }
}

// Preview: private, uses @Preview
@Preview
@Composable
private fun ExampleViewPreview(
  @PreviewParameter(ExamplePreviewProvider::class)
  state: ExampleViewState,
) {
  ExampleView(
    state = state,
    onIntent = {},
  )
}

// Supporting types
internal data class ExampleViewState(
  val title: String,
  val count: Int,
)

internal sealed interface ExampleIntent {
  data object IncrementClick : ExampleIntent
  data class ItemClick(val id: String) : ExampleIntent
}
