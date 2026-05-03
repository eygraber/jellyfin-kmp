@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Server configuration as returned by `GET /System/Configuration`.
 *
 * Only a curated subset of fields is exposed - the actual response has dozens
 * of legacy options that aren't useful in the client. Add fields as needed.
 */
@Serializable
data class ServerConfigurationDto(
  @SerialName("ServerName") val serverName: String? = null,
  @SerialName("PreferredMetadataLanguage") val preferredMetadataLanguage: String? = null,
  @SerialName("MetadataCountryCode") val metadataCountryCode: String? = null,
  @SerialName("EnableMetrics") val enableMetrics: Boolean = false,
  @SerialName("EnableNormalizedItemByNameIds") val enableNormalizedItemByNameIds: Boolean = true,
  @SerialName("IsPortAuthorized") val isPortAuthorized: Boolean = true,
  @SerialName("QuickConnectAvailable") val quickConnectAvailable: Boolean = false,
  @SerialName("EnableCaseSensitiveItemIds") val enableCaseSensitiveItemIds: Boolean = true,
  @SerialName("DisableLiveTvChannelUserDataName") val disableLiveTvChannelUserDataName: Boolean = false,
  @SerialName("MetadataPath") val metadataPath: String? = null,
  @SerialName("MetadataNetworkPath") val metadataNetworkPath: String? = null,
  @SerialName("SortReplaceCharacters") val sortReplaceCharacters: List<String> = emptyList(),
  @SerialName("SortRemoveCharacters") val sortRemoveCharacters: List<String> = emptyList(),
  @SerialName("SortRemoveWords") val sortRemoveWords: List<String> = emptyList(),
  @SerialName("MinResumePct") val minResumePct: Int = 0,
  @SerialName("MaxResumePct") val maxResumePct: Int = 0,
  @SerialName("MinResumeDurationSeconds") val minResumeDurationSeconds: Int = 0,
  @SerialName("MinAudiobookResume") val minAudiobookResume: Int = 0,
  @SerialName("MaxAudiobookResume") val maxAudiobookResume: Int = 0,
  @SerialName("InactiveSessionThreshold") val inactiveSessionThreshold: Int = 0,
  @SerialName("LibraryMonitorDelay") val libraryMonitorDelay: Int = 0,
  @SerialName("LibraryUpdateDuration") val libraryUpdateDuration: Int = 0,
  @SerialName("CachePath") val cachePath: String? = null,
)
