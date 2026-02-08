package com.eygraber.jellyfin.screens.collection.items

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.collection.items.model.CollectionItemsModel
import com.eygraber.jellyfin.screens.collection.items.model.CollectionItemsModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class CollectionItemsCompositor(
  private val key: CollectionItemsKey,
  private val navigator: CollectionItemsNavigator,
  private val itemsModel: CollectionItemsModel,
) : ViceCompositor<CollectionItemsIntent, CollectionItemsViewState> {

  @Composable
  override fun composite(): CollectionItemsViewState {
    val modelState = itemsModel.currentState()

    LaunchedEffect(Unit) {
      itemsModel.loadInitial(key.collectionId)
    }

    return CollectionItemsViewState(
      collectionName = modelState.collectionName,
      items = modelState.items,
      isLoading = modelState.isLoading,
      isLoadingMore = modelState.isLoadingMore,
      error = modelState.error?.toViewError(),
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.items.isEmpty(),
      hasMoreItems = modelState.hasMoreItems,
    )
  }

  override suspend fun onIntent(intent: CollectionItemsIntent) {
    when(intent) {
      CollectionItemsIntent.LoadMore -> itemsModel.loadMore(key.collectionId)
      CollectionItemsIntent.RetryLoad -> itemsModel.loadInitial(key.collectionId)
      is CollectionItemsIntent.SelectItem -> navigator.navigateToItemDetail(intent.itemId)
      CollectionItemsIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun CollectionItemsModelError.toViewError(): CollectionItemsError = when(this) {
    CollectionItemsModelError.LoadFailed -> CollectionItemsError.Network()
  }
}
