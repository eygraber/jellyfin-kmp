package com.eygraber.jellyfin.data.admin.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.admin.AdminUser
import com.eygraber.jellyfin.data.admin.AdminUserPolicy
import com.eygraber.jellyfin.data.admin.UserAdminRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [UserAdminRepository].
 *
 * Stateless: every call delegates to [AdminRemoteDataSource].
 */
@ContributesBinding(AppScope::class)
internal class DefaultUserAdminRepository(
  private val remoteDataSource: AdminRemoteDataSource,
) : UserAdminRepository {
  override suspend fun getUsers(
    isHidden: Boolean?,
    isDisabled: Boolean?,
  ): JellyfinResult<List<AdminUser>> =
    remoteDataSource.getUsers(isHidden = isHidden, isDisabled = isDisabled)

  override suspend fun getUser(userId: String): JellyfinResult<AdminUser> =
    remoteDataSource.getUser(userId = userId)

  override suspend fun createUser(name: String, password: String?): JellyfinResult<AdminUser> =
    remoteDataSource.createUser(name = name, password = password)

  override suspend fun updateUserPolicy(
    userId: String,
    policy: AdminUserPolicy,
  ): JellyfinResult<Unit> =
    remoteDataSource.updateUserPolicy(userId = userId, policy = policy)

  override suspend fun setUserPassword(
    userId: String,
    newPassword: String,
    currentPassword: String?,
    resetPassword: Boolean,
  ): JellyfinResult<Unit> =
    remoteDataSource.setUserPassword(
      userId = userId,
      newPassword = newPassword,
      currentPassword = currentPassword,
      resetPassword = resetPassword,
    )

  override suspend fun deleteUser(userId: String): JellyfinResult<Unit> =
    remoteDataSource.deleteUser(userId = userId)
}
