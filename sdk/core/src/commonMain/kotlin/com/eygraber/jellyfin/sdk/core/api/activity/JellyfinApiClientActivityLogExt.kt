package com.eygraber.jellyfin.sdk.core.api.activity

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient

val JellyfinApiClient.activityLogApi: ActivityLogApi
  get() = ActivityLogApi(this)
