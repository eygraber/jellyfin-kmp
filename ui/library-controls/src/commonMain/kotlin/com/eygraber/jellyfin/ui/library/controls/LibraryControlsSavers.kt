package com.eygraber.jellyfin.ui.library.controls

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.SortOrder

/**
 * `rememberSaveable` saver for [LibrarySortConfig]. Persists across composition disposal — needed
 * because Nav3's single-pane scene strategy disposes a screen's composition on forward navigation,
 * which would otherwise reset sort selection when the user comes back.
 */
val LibrarySortConfigSaver: Saver<LibrarySortConfig, Any> = listSaver(
  save = { sort -> listOf(sort.sortBy.name, sort.sortOrder.name) },
  restore = { saved ->
    LibrarySortConfig(
      sortBy = ItemSortBy.valueOf(saved[0]),
      sortOrder = SortOrder.valueOf(saved[1]),
    )
  },
)

/**
 * `rememberSaveable` saver for [LibraryFilters]. Two parallel lists keep the saved form trivially
 * round-trippable through Compose's default saved-state registry on every supported platform.
 */
val LibraryFiltersSaver: Saver<LibraryFilters, Any> = listSaver(
  save = { filters -> listOf(filters.genres, filters.years.map(Int::toString)) },
  restore = { saved ->
    LibraryFilters(
      genres = saved[0],
      years = saved[1].map(String::toInt),
    )
  },
)

@Composable
fun rememberLibrarySortConfig(
  initial: LibrarySortConfig = LibrarySortConfig(),
): MutableState<LibrarySortConfig> = rememberSaveable(stateSaver = LibrarySortConfigSaver) {
  mutableStateOf(initial)
}

@Composable
fun rememberLibraryFilters(
  initial: LibraryFilters = LibraryFilters(),
): MutableState<LibraryFilters> = rememberSaveable(stateSaver = LibraryFiltersSaver) {
  mutableStateOf(initial)
}
