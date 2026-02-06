package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerDiscoveryInfo(
  @SerialName("Address") val address: String,
  @SerialName("Id") val id: String,
  @SerialName("Name") val name: String? = null,
  @SerialName("EndpointAddress") val endpointAddress: String? = null,
)
