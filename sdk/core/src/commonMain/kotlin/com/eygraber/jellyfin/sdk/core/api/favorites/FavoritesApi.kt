package com.eygraber.jellyfin.sdk.core.api.favorites

import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.api.BaseApi
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.UserItemDataDto

class FavoritesApi(
  apiClient: JellyfinApiClient,
) : BaseApi(apiClient) {
  /**
   * Marks an item as a favorite for the specified user.
   *
   * @param userId The user ID.
   * @param itemId The item ID to mark as a favorite.
   */
  suspend fun addFavorite(
    userId: String,
    itemId: String,
  ): SdkResult<UserItemDataDto> = post<UserItemDataDto, Unit>(
    path = "Users/$userId/FavoriteItems/$itemId",
  )

  /**
   * Removes an item from the user's favorites.
   *
   * @param userId The user ID.
   * @param itemId The item ID to remove from favorites.
   */
  suspend fun removeFavorite(
    userId: String,
    itemId: String,
  ): SdkResult<UserItemDataDto> = delete(
    path = "Users/$userId/FavoriteItems/$itemId",
  )
}
