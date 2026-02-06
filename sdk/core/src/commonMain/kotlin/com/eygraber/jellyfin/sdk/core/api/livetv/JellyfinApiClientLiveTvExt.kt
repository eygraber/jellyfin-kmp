package com.eygraber.jellyfin.sdk.core.api.livetv

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient

val JellyfinApiClient.liveTvApi: LiveTvApi
  get() = LiveTvApi(this)
