@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaybackInfoResponse(
  @SerialName("MediaSources") val mediaSources: List<MediaSourceInfo> = emptyList(),
  @SerialName("PlaySessionId") val playSessionId: String? = null,
)

@Serializable
data class MediaSourceInfo(
  @SerialName("Id") val id: String? = null,
  @SerialName("Path") val path: String? = null,
  @SerialName("Name") val name: String? = null,
  @SerialName("Container") val container: String? = null,
  @SerialName("Size") val size: Long? = null,
  @SerialName("Bitrate") val bitrate: Int? = null,
  @SerialName("SupportsTranscoding") val supportsTranscoding: Boolean = false,
  @SerialName("SupportsDirectStream") val supportsDirectStream: Boolean = false,
  @SerialName("SupportsDirectPlay") val supportsDirectPlay: Boolean = false,
  @SerialName("IsRemote") val isRemote: Boolean = false,
  @SerialName("RunTimeTicks") val runTimeTicks: Long? = null,
  @SerialName("TranscodingUrl") val transcodingUrl: String? = null,
  @SerialName("DirectStreamUrl") val directStreamUrl: String? = null,
  @SerialName("MediaStreams") val mediaStreams: List<MediaStream> = emptyList(),
)

@Serializable
data class MediaStream(
  @SerialName("Codec") val codec: String? = null,
  @SerialName("Language") val language: String? = null,
  @SerialName("DisplayTitle") val displayTitle: String? = null,
  @SerialName("DisplayLanguage") val displayLanguage: String? = null,
  @SerialName("Type") val type: String? = null,
  @SerialName("Index") val index: Int? = null,
  @SerialName("IsDefault") val isDefault: Boolean = false,
  @SerialName("IsExternal") val isExternal: Boolean = false,
  @SerialName("IsForced") val isForced: Boolean = false,
  @SerialName("Height") val height: Int? = null,
  @SerialName("Width") val width: Int? = null,
  @SerialName("BitRate") val bitRate: Int? = null,
  @SerialName("Channels") val channels: Int? = null,
  @SerialName("SampleRate") val sampleRate: Int? = null,
  @SerialName("DeliveryUrl") val deliveryUrl: String? = null,
)
