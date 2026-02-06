package com.eygraber.jellyfin.sdk.core.api.user

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient

val JellyfinApiClient.userApi: UserApi
  get() = UserApi(this)
