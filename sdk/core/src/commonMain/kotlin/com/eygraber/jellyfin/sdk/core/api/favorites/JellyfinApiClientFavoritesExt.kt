package com.eygraber.jellyfin.sdk.core.api.favorites

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient

val JellyfinApiClient.favoritesApi: FavoritesApi
  get() = FavoritesApi(this)
