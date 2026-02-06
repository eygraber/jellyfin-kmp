package com.eygraber.jellyfin.sdk.core

data class ServerInfo(
  val baseUrl: String,
  val accessToken: String? = null,
  val userId: String? = null,
)
