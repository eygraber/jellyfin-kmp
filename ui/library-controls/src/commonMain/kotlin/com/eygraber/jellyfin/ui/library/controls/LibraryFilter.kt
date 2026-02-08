package com.eygraber.jellyfin.ui.library.controls

import androidx.compose.runtime.Immutable

/**
 * Represents the active filters applied to a library view.
 */
@Immutable
data class LibraryFilters(
  val genres: List<String> = emptyList(),
  val years: List<Int> = emptyList(),
) {
  val hasActiveFilters: Boolean
    get() = genres.isNotEmpty() || years.isNotEmpty()

  val activeFilterCount: Int
    get() = genres.size + years.size
}
