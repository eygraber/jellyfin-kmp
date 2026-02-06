package com.eygraber.jellyfin.sdk.core.api.system

import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.api.BaseApi
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.BrandingConfiguration
import com.eygraber.jellyfin.sdk.core.model.PublicSystemInfo
import com.eygraber.jellyfin.sdk.core.model.SystemInfo

class SystemApi(
  apiClient: JellyfinApiClient,
) : BaseApi(apiClient) {
  /**
   * Gets information about the server.
   * Requires authentication.
   */
  suspend fun getSystemInfo(): SdkResult<SystemInfo> =
    get(path = "System/Info")

  /**
   * Gets public information about the server.
   * Does not require authentication.
   */
  suspend fun getPublicSystemInfo(): SdkResult<PublicSystemInfo> =
    get(path = "System/Info/Public")

  /**
   * Pings the server to check connectivity.
   * Does not require authentication.
   * Returns the ping response string (usually "Jellyfin Server").
   */
  suspend fun ping(): SdkResult<String> =
    get(path = "System/Ping")

  /**
   * Gets the branding configuration for the server.
   * Does not require authentication.
   */
  suspend fun getBrandingConfiguration(): SdkResult<BrandingConfiguration> =
    get(path = "Branding/Configuration")
}
