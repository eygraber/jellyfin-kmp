package com.eygraber.jellyfin.sdk.core

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import io.ktor.client.engine.HttpClientEngine

class JellyfinSdk(
  val clientInfo: ClientInfo,
  val deviceInfo: DeviceInfo,
) {
  fun createApiClient(
    serverInfo: ServerInfo,
    httpClientEngine: HttpClientEngine? = null,
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
