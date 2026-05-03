package com.eygraber.jellyfin.sdk.core.api.tasks

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient

val JellyfinApiClient.scheduledTasksApi: ScheduledTasksApi
  get() = ScheduledTasksApi(this)
