package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateByNameRequest(
  @SerialName("Username") val username: String,
  @SerialName("Pw") val password: String,
)
