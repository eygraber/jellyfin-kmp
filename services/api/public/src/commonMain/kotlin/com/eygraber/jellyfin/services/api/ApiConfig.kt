package com.eygraber.jellyfin.services.api

/**
 * Configuration for the Jellyfin API client.
 *
 * @param baseUrl The base URL of the Jellyfin server (e.g., "https://jellyfin.example.com").
 * @param clientName The name of this client (e.g., "Jellyfin for Android").
 * @param clientVersion The version of this client.
 * @param deviceName The name of the device running this client.
 * @param deviceId A unique identifier for this device.
 * @param connectTimeoutMs Connection timeout in milliseconds.
 * @param requestTimeoutMs Request timeout in milliseconds.
 * @param socketTimeoutMs Socket timeout in milliseconds.
 */
data class ApiConfig(
  val baseUrl: String,
  val clientName: String,
  val clientVersion: String,
  val deviceName: String,
  val deviceId: String,
  val connectTimeoutMs: Long = DEFAULT_CONNECT_TIMEOUT_MS,
  val requestTimeoutMs: Long = DEFAULT_REQUEST_TIMEOUT_MS,
  val socketTimeoutMs: Long = DEFAULT_SOCKET_TIMEOUT_MS,
) {
  companion object {
    const val DEFAULT_CONNECT_TIMEOUT_MS = 15_000L
    const val DEFAULT_REQUEST_TIMEOUT_MS = 30_000L
    const val DEFAULT_SOCKET_TIMEOUT_MS = 30_000L
  }
}
