@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuickConnectResult(
  @SerialName("Authenticated") val authenticated: Boolean = false,
  @SerialName("Secret") val secret: String? = null,
  @SerialName("Code") val code: String? = null,
  @SerialName("DateAdded") val dateAdded: String? = null,
)
