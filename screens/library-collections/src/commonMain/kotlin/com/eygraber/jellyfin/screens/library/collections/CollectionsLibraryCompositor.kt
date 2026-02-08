package com.eygraber.jellyfin.screens.library.collections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.library.collections.model.CollectionsLibraryModel
import com.eygraber.jellyfin.screens.library.collections.model.CollectionsLibraryModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class CollectionsLibraryCompositor(
  private val key: CollectionsLibraryKey,
  private val navigator: CollectionsLibraryNavigator,
  private val collectionsModel: CollectionsLibraryModel,
) : ViceCompositor<CollectionsLibraryIntent, CollectionsLibraryViewState> {

  @Composable
  override fun composite(): CollectionsLibraryViewState {
    val modelState = collectionsModel.currentState()

    LaunchedEffect(Unit) {
      collectionsModel.loadInitial(key.libraryId)
    }

    return CollectionsLibraryViewState(
      collections = modelState.collections,
      isLoading = modelState.isLoading,
      isLoadingMore = modelState.isLoadingMore,
      error = modelState.error?.toViewError(),
      isEmpty = !modelState.isLoading &&
        modelState.error == null &&
        modelState.collections.isEmpty(),
      hasMoreItems = modelState.hasMoreItems,
    )
  }

  override suspend fun onIntent(intent: CollectionsLibraryIntent) {
    when(intent) {
      CollectionsLibraryIntent.LoadMore -> collectionsModel.loadMore(key.libraryId)
      CollectionsLibraryIntent.Refresh -> collectionsModel.refresh(key.libraryId)
      CollectionsLibraryIntent.RetryLoad -> collectionsModel.loadInitial(key.libraryId)
      is CollectionsLibraryIntent.SelectCollection ->
        navigator.navigateToCollectionItems(intent.collectionId)

      CollectionsLibraryIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun CollectionsLibraryModelError.toViewError(): CollectionsLibraryError = when(this) {
    CollectionsLibraryModelError.LoadFailed -> CollectionsLibraryError.Network()
  }
}
