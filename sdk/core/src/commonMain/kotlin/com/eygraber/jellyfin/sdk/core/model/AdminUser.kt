@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for `POST /Users/New`.
 */
@Serializable
data class CreateUserRequest(
  @SerialName("Name") val name: String,
  @SerialName("Password") val password: String? = null,
)

/**
 * Request body for `POST /Users/{userId}/Password`.
 *
 * `currentPassword` may be omitted for admin password resets that go through
 * `/Users/{userId}/Password` with `resetPassword=true`.
 */
@Serializable
data class UpdateUserPasswordRequest(
  @SerialName("CurrentPw") val currentPassword: String? = null,
  @SerialName("NewPw") val newPassword: String,
  @SerialName("ResetPassword") val resetPassword: Boolean = false,
)
