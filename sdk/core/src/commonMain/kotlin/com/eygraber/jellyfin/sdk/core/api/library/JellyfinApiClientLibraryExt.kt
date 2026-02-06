package com.eygraber.jellyfin.sdk.core.api.library

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient

val JellyfinApiClient.libraryApi: LibraryApi
  get() = LibraryApi(this)
