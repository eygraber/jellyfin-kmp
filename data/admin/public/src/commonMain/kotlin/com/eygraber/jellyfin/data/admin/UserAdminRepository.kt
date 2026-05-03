package com.eygraber.jellyfin.data.admin

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for administrative user operations.
 *
 * All operations require an authenticated session whose user is an
 * administrator. The repository does not enforce that client-side - the
 * server returns 403 for unauthorized requests, which surfaces as a
 * [JellyfinResult.Error.Detailed].
 *
 * Stateless: every call delegates to the network. Callers should cache
 * results at the screen level if they need to retain them across recompositions.
 */
interface UserAdminRepository {
  /**
   * Lists all users on the server.
   *
   * @param isHidden Filter to hidden / non-hidden users when set.
   * @param isDisabled Filter to disabled / enabled users when set.
   */
  suspend fun getUsers(
    isHidden: Boolean? = null,
    isDisabled: Boolean? = null,
  ): JellyfinResult<List<AdminUser>>

  /**
   * Gets a single user by ID.
   */
  suspend fun getUser(userId: String): JellyfinResult<AdminUser>

  /**
   * Creates a new user with the given name and optional initial password.
   */
  suspend fun createUser(
    name: String,
    password: String? = null,
  ): JellyfinResult<AdminUser>

  /**
   * Updates a user's permissions/policy.
   */
  suspend fun updateUserPolicy(
    userId: String,
    policy: AdminUserPolicy,
  ): JellyfinResult<Unit>

  /**
   * Sets a new password for a user. The current password is required when
   * changing your own password; admin password resets pass [resetPassword] = true
   * with no [currentPassword].
   */
  suspend fun setUserPassword(
    userId: String,
    newPassword: String,
    currentPassword: String? = null,
    resetPassword: Boolean = false,
  ): JellyfinResult<Unit>

  /**
   * Deletes a user.
   */
  suspend fun deleteUser(userId: String): JellyfinResult<Unit>
}
