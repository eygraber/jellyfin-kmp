package com.eygraber.jellyfin.sdk.core.api.media

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient

val JellyfinApiClient.mediaApi: MediaApi
  get() = MediaApi(this)
