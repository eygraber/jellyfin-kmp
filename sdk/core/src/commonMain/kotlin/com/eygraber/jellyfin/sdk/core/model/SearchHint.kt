package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchHintResult(
  @SerialName("SearchHints") val searchHints: List<SearchHint> = emptyList(),
  @SerialName("TotalRecordCount") val totalRecordCount: Int = 0,
)

@Serializable
data class SearchHint(
  @SerialName("ItemId") val itemId: String? = null,
  @SerialName("Id") val id: String? = null,
  @SerialName("Name") val name: String? = null,
  @SerialName("Type") val type: String? = null,
  @SerialName("MediaType") val mediaType: String? = null,
  @SerialName("RunTimeTicks") val runTimeTicks: Long? = null,
  @SerialName("ProductionYear") val productionYear: Int? = null,
  @SerialName("PrimaryImageTag") val primaryImageTag: String? = null,
  @SerialName("ThumbImageTag") val thumbImageTag: String? = null,
  @SerialName("ThumbImageItemId") val thumbImageItemId: String? = null,
  @SerialName("BackdropImageTag") val backdropImageTag: String? = null,
  @SerialName("BackdropImageItemId") val backdropImageItemId: String? = null,
  @SerialName("Series") val series: String? = null,
  @SerialName("Album") val album: String? = null,
  @SerialName("AlbumArtist") val albumArtist: String? = null,
  @SerialName("Artists") val artists: List<String> = emptyList(),
  @SerialName("ChannelName") val channelName: String? = null,
)
