@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaybackStartInfo(
  @SerialName("ItemId") val itemId: String,
  @SerialName("MediaSourceId") val mediaSourceId: String? = null,
  @SerialName("AudioStreamIndex") val audioStreamIndex: Int? = null,
  @SerialName("SubtitleStreamIndex") val subtitleStreamIndex: Int? = null,
  @SerialName("PlaySessionId") val playSessionId: String? = null,
  @SerialName("CanSeek") val canSeek: Boolean = true,
  @SerialName("IsPaused") val isPaused: Boolean = false,
  @SerialName("IsMuted") val isMuted: Boolean = false,
  @SerialName("PositionTicks") val positionTicks: Long? = null,
  @SerialName("PlayMethod") val playMethod: String? = null,
)

@Serializable
data class PlaybackProgressInfo(
  @SerialName("ItemId") val itemId: String,
  @SerialName("MediaSourceId") val mediaSourceId: String? = null,
  @SerialName("AudioStreamIndex") val audioStreamIndex: Int? = null,
  @SerialName("SubtitleStreamIndex") val subtitleStreamIndex: Int? = null,
  @SerialName("PlaySessionId") val playSessionId: String? = null,
  @SerialName("CanSeek") val canSeek: Boolean = true,
  @SerialName("IsPaused") val isPaused: Boolean = false,
  @SerialName("IsMuted") val isMuted: Boolean = false,
  @SerialName("PositionTicks") val positionTicks: Long? = null,
  @SerialName("PlayMethod") val playMethod: String? = null,
  @SerialName("VolumeLevel") val volumeLevel: Int? = null,
)

@Serializable
data class PlaybackStopInfo(
  @SerialName("ItemId") val itemId: String,
  @SerialName("MediaSourceId") val mediaSourceId: String? = null,
  @SerialName("PlaySessionId") val playSessionId: String? = null,
  @SerialName("PositionTicks") val positionTicks: Long? = null,
)
