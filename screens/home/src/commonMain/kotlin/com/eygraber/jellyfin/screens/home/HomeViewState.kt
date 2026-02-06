package com.eygraber.jellyfin.screens.home

import androidx.compose.runtime.Immutable

@Immutable
data class HomeViewState(
  val userName: String = "",
  val isLoading: Boolean = true,
  val error: HomeError? = null,
  val isRefreshing: Boolean = false,
  val continueWatchingState: ContinueWatchingState = ContinueWatchingState.Loading,
  val nextUpState: NextUpState = NextUpState.Loading,
  val recentlyAddedState: RecentlyAddedState = RecentlyAddedState.Loading,
  val librariesState: LibrariesState = LibrariesState.Loading,
) {
  companion object {
    val Loading = HomeViewState(isLoading = true)
  }
}

@Immutable
sealed interface HomeError {
  val message: String

  data class Network(override val message: String = "Unable to connect to server") : HomeError
  data class Generic(override val message: String = "Something went wrong") : HomeError
}

@Immutable
sealed interface ContinueWatchingState {
  data object Loading : ContinueWatchingState
  data object Empty : ContinueWatchingState
  data object Error : ContinueWatchingState

  data class Loaded(
    val items: List<ContinueWatchingItem>,
  ) : ContinueWatchingState
}

@Immutable
data class ContinueWatchingItem(
  val id: String,
  val name: String,
  val type: String,
  val seriesName: String?,
  val seasonName: String?,
  val indexNumber: Int?,
  val parentIndexNumber: Int?,
  val progressPercent: Float,
  val imageUrl: String,
  val backdropImageUrl: String?,
) {
  val displayName: String
    get() = when {
      seriesName != null && parentIndexNumber != null && indexNumber != null ->
        "$seriesName - S$parentIndexNumber:E$indexNumber"

      seriesName != null -> "$seriesName - $name"
      else -> name
    }

  val subtitle: String?
    get() = when {
      seriesName != null -> name
      else -> null
    }
}

@Immutable
sealed interface NextUpState {
  data object Loading : NextUpState
  data object Empty : NextUpState
  data object Error : NextUpState

  data class Loaded(
    val items: List<NextUpItem>,
  ) : NextUpState
}

@Immutable
data class NextUpItem(
  val id: String,
  val name: String,
  val seriesName: String?,
  val seasonName: String?,
  val indexNumber: Int?,
  val parentIndexNumber: Int?,
  val imageUrl: String,
  val backdropImageUrl: String?,
) {
  val displayName: String
    get() = when {
      seriesName != null && parentIndexNumber != null && indexNumber != null ->
        "S$parentIndexNumber:E$indexNumber"

      else -> name
    }

  val subtitle: String
    get() = name
}

@Immutable
sealed interface RecentlyAddedState {
  data object Loading : RecentlyAddedState
  data object Empty : RecentlyAddedState
  data object Error : RecentlyAddedState

  data class Loaded(
    val items: List<RecentlyAddedItem>,
  ) : RecentlyAddedState
}

@Immutable
data class RecentlyAddedItem(
  val id: String,
  val name: String,
  val type: String,
  val productionYear: Int?,
  val imageUrl: String,
  val seriesName: String?,
)

@Immutable
sealed interface LibrariesState {
  data object Loading : LibrariesState
  data object Empty : LibrariesState
  data object Error : LibrariesState

  data class Loaded(
    val libraries: List<LibraryView>,
  ) : LibrariesState
}

@Immutable
data class LibraryView(
  val id: String,
  val name: String,
  val collectionType: CollectionType,
  val imageUrl: String?,
)

enum class CollectionType(val apiValue: String) {
  Movies("movies"),
  TvShows("tvshows"),
  Music("music"),
  MusicVideos("musicvideos"),
  Collections("boxsets"),
  Playlists("playlists"),
  LiveTv("livetv"),
  Photos("photos"),
  HomeVideos("homevideos"),
  Books("books"),
  Unknown(""),
  ;

  companion object {
    fun fromApiValue(value: String?): CollectionType =
      entries.firstOrNull { it.apiValue.equals(other = value, ignoreCase = true) } ?: Unknown
  }
}
