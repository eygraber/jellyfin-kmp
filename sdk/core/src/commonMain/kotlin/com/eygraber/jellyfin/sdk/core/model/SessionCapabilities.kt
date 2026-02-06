@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionCapabilities(
  @SerialName("PlayableMediaTypes") val playableMediaTypes: List<String> = emptyList(),
  @SerialName("SupportedCommands") val supportedCommands: List<String> = emptyList(),
  @SerialName("SupportsMediaControl") val supportsMediaControl: Boolean = false,
  @SerialName("SupportsPersistentIdentifier") val supportsPersistentIdentifier: Boolean = true,
  @SerialName("Id") val id: String? = null,
)
