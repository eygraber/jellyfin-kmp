package com.eygraber.jellyfin.screens.library.genres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.library.genres.model.GenresLibraryModel
import com.eygraber.jellyfin.screens.library.genres.model.GenresLibraryModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class GenresLibraryCompositor(
  private val key: GenresLibraryKey,
  private val navigator: GenresLibraryNavigator,
  private val genresModel: GenresLibraryModel,
) : ViceCompositor<GenresLibraryIntent, GenresLibraryViewState> {

  @Composable
  override fun composite(): GenresLibraryViewState {
    val modelState = genresModel.currentState()

    LaunchedEffect(Unit) {
      genresModel.loadGenres(key.libraryId)
    }

    return GenresLibraryViewState(
      genres = modelState.genres,
      isLoading = modelState.isLoading,
      error = modelState.error?.toViewError(),
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.genres.isEmpty(),
    )
  }

  override suspend fun onIntent(intent: GenresLibraryIntent) {
    when(intent) {
      GenresLibraryIntent.RetryLoad -> genresModel.loadGenres(key.libraryId)
      is GenresLibraryIntent.SelectGenre ->
        navigator.navigateToGenreItems(libraryId = key.libraryId, genreName = intent.genreName)

      GenresLibraryIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun GenresLibraryModelError.toViewError(): GenresLibraryError = when(this) {
    GenresLibraryModelError.LoadFailed -> GenresLibraryError.Network()
  }
}
