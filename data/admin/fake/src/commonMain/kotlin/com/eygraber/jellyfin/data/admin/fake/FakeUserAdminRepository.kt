@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.data.admin.fake

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.admin.AdminUser
import com.eygraber.jellyfin.data.admin.AdminUserPolicy
import com.eygraber.jellyfin.data.admin.UserAdminRepository

/**
 * In-memory fake of [UserAdminRepository] for tests.
 *
 * Maintains a mutable map of users keyed by id. Tests can seed initial state
 * via the [users] property, override the next call's result via [nextResult],
 * or inspect mutations via [createdUsers], [updatedPolicies], [passwordChanges],
 * and [deletedUserIds].
 */
class FakeUserAdminRepository(
  initial: List<AdminUser> = emptyList(),
) : UserAdminRepository {
  private val mutableUsers = initial.associateBy { it.id }.toMutableMap()

  /**
   * Snapshot of the current users.
   */
  val users: List<AdminUser> get() = mutableUsers.values.toList()

  /**
   * If non-null, the next call returns this result instead of the default
   * success path. Cleared after a single use.
   */
  var nextResult: JellyfinResult<Any?>? = null

  val createdUsers: MutableList<Pair<String, String?>> = mutableListOf()
  val updatedPolicies: MutableMap<String, AdminUserPolicy> = mutableMapOf()
  val passwordChanges: MutableList<PasswordChange> = mutableListOf()
  val deletedUserIds: MutableList<String> = mutableListOf()

  override suspend fun getUsers(
    isHidden: Boolean?,
    isDisabled: Boolean?,
  ): JellyfinResult<List<AdminUser>> = consumeOverride()
    ?: JellyfinResult.Success(
      users.filter { user ->
        (isHidden == null || user.policy.isHidden == isHidden) &&
          (isDisabled == null || user.policy.isDisabled == isDisabled)
      },
    )

  override suspend fun getUser(userId: String): JellyfinResult<AdminUser> = consumeOverride()
    ?: mutableUsers[userId]?.let { JellyfinResult.Success(it) }
    ?: JellyfinResult.Error(message = "Not found", isEphemeral = false)

  override suspend fun createUser(name: String, password: String?): JellyfinResult<AdminUser> {
    consumeOverride<AdminUser>()?.let { return it }
    createdUsers += name to password
    val user = AdminUser(
      id = "fake-${mutableUsers.size + 1}",
      name = name,
      hasPassword = password != null,
      hasConfiguredPassword = password != null,
      policy = AdminUserPolicy(
        isAdministrator = false,
        isHidden = false,
        isDisabled = false,
        enableUserPreferenceAccess = true,
        enableRemoteAccess = true,
      ),
    )
    mutableUsers[user.id] = user
    return JellyfinResult.Success(user)
  }

  override suspend fun updateUserPolicy(
    userId: String,
    policy: AdminUserPolicy,
  ): JellyfinResult<Unit> {
    consumeOverride<Unit>()?.let { return it }
    updatedPolicies[userId] = policy
    mutableUsers[userId]?.let { existing ->
      mutableUsers[userId] = existing.copy(policy = policy)
    }
    return JellyfinResult.Success(Unit)
  }

  override suspend fun setUserPassword(
    userId: String,
    newPassword: String,
    currentPassword: String?,
    resetPassword: Boolean,
  ): JellyfinResult<Unit> {
    consumeOverride<Unit>()?.let { return it }
    passwordChanges += PasswordChange(
      userId = userId,
      newPassword = newPassword,
      currentPassword = currentPassword,
      resetPassword = resetPassword,
    )
    return JellyfinResult.Success(Unit)
  }

  override suspend fun deleteUser(userId: String): JellyfinResult<Unit> {
    consumeOverride<Unit>()?.let { return it }
    deletedUserIds += userId
    mutableUsers.remove(userId)
    return JellyfinResult.Success(Unit)
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T> consumeOverride(): JellyfinResult<T>? {
    val override = nextResult ?: return null
    nextResult = null
    return override as JellyfinResult<T>
  }

  data class PasswordChange(
    val userId: String,
    val newPassword: String,
    val currentPassword: String?,
    val resetPassword: Boolean,
  )
}
