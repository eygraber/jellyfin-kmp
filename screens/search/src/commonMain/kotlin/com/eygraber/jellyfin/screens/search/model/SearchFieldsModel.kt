package com.eygraber.jellyfin.screens.search.model

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.screens.search.SearchFieldsState
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.Channel

@Inject
@SingleIn(ScreenScope::class)
class SearchFieldsModel : ViceSource<SearchFieldsState> {
  private val mutations = Channel<TextFieldState.() -> Unit>()

  @Composable
  override fun currentState(): SearchFieldsState {
    val query = rememberTextFieldState()
    LaunchedEffect(query) {
      for(mutate in mutations) query.mutate()
    }
    return SearchFieldsState(query = query)
  }

  suspend fun setQuery(query: String) {
    mutations.send { setTextAndPlaceCursorAtEnd(query) }
  }
}
