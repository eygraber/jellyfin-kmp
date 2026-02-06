package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemsResult(
  @SerialName("Items") val items: List<BaseItemDto> = emptyList(),
  @SerialName("TotalRecordCount") val totalRecordCount: Int = 0,
  @SerialName("StartIndex") val startIndex: Int = 0,
)
