package com.eygraber.jellyfin.screens.search.model

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.screens.search.SearchFieldsState
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(ScreenScope::class)
class SearchFieldsModel : ViceSource<SearchFieldsState> {
  private val state = SearchFieldsState(
    query = TextFieldState(),
  )

  @Composable
  override fun currentState() = state

  fun setQuery(query: String) {
    state.query.setTextAndPlaceCursorAtEnd(query)
  }
}
