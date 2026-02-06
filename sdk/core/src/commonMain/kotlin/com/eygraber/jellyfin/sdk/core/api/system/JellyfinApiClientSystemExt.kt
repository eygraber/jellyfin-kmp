package com.eygraber.jellyfin.sdk.core.api.system

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient

val JellyfinApiClient.systemApi: SystemApi
  get() = SystemApi(this)
