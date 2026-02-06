package com.eygraber.jellyfin.sdk.core

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient

class JellyfinSdk(
  val clientInfo: ClientInfo,
  val deviceInfo: DeviceInfo,
) {
  fun createApiClient(
    serverInfo: ServerInfo,
    httpClientEngine: io.ktor.client.engine.HttpClientEngine? = null,
  ): JellyfinApiClient = JellyfinApiClient(
    clientInfo = clientInfo,
    deviceInfo = deviceInfo,
    serverInfo = serverInfo,
    httpClientEngine = httpClientEngine,
  )
}

fun createJellyfinSdk(
  clientInfo: ClientInfo,
  deviceInfo: DeviceInfo,
): JellyfinSdk = JellyfinSdk(
  clientInfo = clientInfo,
  deviceInfo = deviceInfo,
)
