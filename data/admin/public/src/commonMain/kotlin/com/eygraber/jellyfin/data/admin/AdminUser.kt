@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.data.admin

/**
 * A Jellyfin user as exposed to admin features.
 *
 * Distinct from the auth-layer user concept: this includes admin-only state
 * such as policy, hidden/disabled flags, and password presence.
 */
data class AdminUser(
  val id: String,
  val name: String,
  val hasPassword: Boolean,
  val hasConfiguredPassword: Boolean,
  val policy: AdminUserPolicy,
)

/**
 * Permissions and admin flags applied to an [AdminUser].
 */
data class AdminUserPolicy(
  val isAdministrator: Boolean,
  val isHidden: Boolean,
  val isDisabled: Boolean,
  val enableUserPreferenceAccess: Boolean,
  val enableRemoteAccess: Boolean,
)
