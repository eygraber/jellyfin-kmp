@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveTvChannelResult(
  @SerialName("Items") val items: List<BaseItemDto> = emptyList(),
  @SerialName("TotalRecordCount") val totalRecordCount: Int = 0,
)

@Serializable
data class LiveTvProgramResult(
  @SerialName("Items") val items: List<BaseItemDto> = emptyList(),
  @SerialName("TotalRecordCount") val totalRecordCount: Int = 0,
)

@Serializable
data class LiveTvRecordingResult(
  @SerialName("Items") val items: List<BaseItemDto> = emptyList(),
  @SerialName("TotalRecordCount") val totalRecordCount: Int = 0,
)

@Serializable
data class LiveTvTimerInfoDto(
  @SerialName("Id") val id: String? = null,
  @SerialName("ChannelId") val channelId: String? = null,
  @SerialName("ChannelName") val channelName: String? = null,
  @SerialName("ProgramId") val programId: String? = null,
  @SerialName("Name") val name: String? = null,
  @SerialName("Overview") val overview: String? = null,
  @SerialName("StartDate") val startDate: String? = null,
  @SerialName("EndDate") val endDate: String? = null,
  @SerialName("Status") val status: String? = null,
  @SerialName("IsPrePaddingRequired") val isPrePaddingRequired: Boolean = false,
  @SerialName("IsPostPaddingRequired") val isPostPaddingRequired: Boolean = false,
  @SerialName("PrePaddingSeconds") val prePaddingSeconds: Int = 0,
  @SerialName("PostPaddingSeconds") val postPaddingSeconds: Int = 0,
)
