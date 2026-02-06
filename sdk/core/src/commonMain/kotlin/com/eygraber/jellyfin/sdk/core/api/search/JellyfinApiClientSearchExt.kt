package com.eygraber.jellyfin.sdk.core.api.search

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient

val JellyfinApiClient.searchApi: SearchApi
  get() = SearchApi(this)
