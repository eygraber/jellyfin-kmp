package com.eygraber.jellyfin.screens.genre.items

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.genre.items.model.GenreItemsModel
import com.eygraber.jellyfin.screens.genre.items.model.GenreItemsModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class GenreItemsCompositor(
  private val key: GenreItemsKey,
  private val navigator: GenreItemsNavigator,
  private val itemsModel: GenreItemsModel,
) : ViceCompositor<GenreItemsIntent, GenreItemsViewState> {

  @Composable
  override fun composite(): GenreItemsViewState {
    val modelState = itemsModel.currentState()

    LaunchedEffect(Unit) {
      itemsModel.loadInitial(libraryId = key.libraryId, genreName = key.genreName)
    }

    return GenreItemsViewState(
      genreName = modelState.genreName,
      items = modelState.items,
      isLoading = modelState.isLoading,
      isLoadingMore = modelState.isLoadingMore,
      error = modelState.error?.toViewError(),
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.items.isEmpty(),
      hasMoreItems = modelState.hasMoreItems,
    )
  }

  override suspend fun onIntent(intent: GenreItemsIntent) {
    when(intent) {
      GenreItemsIntent.LoadMore -> itemsModel.loadMore(libraryId = key.libraryId, genreName = key.genreName)
      GenreItemsIntent.RetryLoad -> itemsModel.loadInitial(libraryId = key.libraryId, genreName = key.genreName)
      is GenreItemsIntent.SelectItem -> navigator.navigateToItemDetail(intent.itemId)
      GenreItemsIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun GenreItemsModelError.toViewError(): GenreItemsError = when(this) {
    GenreItemsModelError.LoadFailed -> GenreItemsError.Network()
  }
}
