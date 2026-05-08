package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.JellyfinSdk
import com.eygraber.jellyfin.sdk.core.createJellyfinSdk
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Metro DI module that provides the [JellyfinSdk] instance.
 *
 * The SDK is created once at app scope and shared across all services.
 * Individual API client instances are created per server connection.
 */
@ContributesTo(AppScope::class)
interface JellyfinSdkProvider {
  @Provides
  @SingleIn(AppScope::class)
  fun provideClientInfo(): ClientInfo = ClientInfo(
    name = "Jellyfin KMP",
    version = "0.1.0",
  )

  @OptIn(ExperimentalUuidApi::class)
  @Provides
  @SingleIn(AppScope::class)
  fun provideDeviceInfo(
    deviceInfoStore: DeviceInfoStore,
  ): DeviceInfo {
    val id = deviceInfoStore.readDeviceId() ?: Uuid.random().toString().also {
      deviceInfoStore.writeDeviceId(it)
    }
    return DeviceInfo(
      name = deviceInfoStore.deviceName,
      id = id,
    )
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideJellyfinSdk(
    clientInfo: ClientInfo,
    deviceInfo: DeviceInfo,
  ): JellyfinSdk = createJellyfinSdk(
    clientInfo = clientInfo,
    deviceInfo = deviceInfo,
  )
}
