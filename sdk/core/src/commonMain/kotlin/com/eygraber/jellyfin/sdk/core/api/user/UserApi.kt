package com.eygraber.jellyfin.sdk.core.api.user

import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.api.BaseApi
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.AuthenticateByNameRequest
import com.eygraber.jellyfin.sdk.core.model.AuthenticationResult
import com.eygraber.jellyfin.sdk.core.model.QuickConnectResult
import com.eygraber.jellyfin.sdk.core.model.SessionCapabilities
import com.eygraber.jellyfin.sdk.core.model.UserDto

class UserApi(
  apiClient: JellyfinApiClient,
) : BaseApi(apiClient) {
  /**
   * Authenticates a user by name and password.
   * Returns an [AuthenticationResult] with access token on success.
   */
  suspend fun authenticateByName(
    username: String,
    password: String,
  ): SdkResult<AuthenticationResult> = post(
    path = "Users/AuthenticateByName",
    body = AuthenticateByNameRequest(
      username = username,
      password = password,
    ),
  )

  /**
   * Gets the currently logged in user.
   * Requires authentication.
   */
  suspend fun getCurrentUser(): SdkResult<UserDto> =
    get(path = "Users/Me")

  /**
   * Gets a user by their ID.
   * Requires authentication.
   */
  suspend fun getUserById(userId: String): SdkResult<UserDto> =
    get(path = "Users/$userId")

  /**
   * Gets all public users on the server.
   * Does not require authentication.
   */
  suspend fun getPublicUsers(): SdkResult<List<UserDto>> =
    get(path = "Users/Public")

  /**
   * Reports session capabilities to the server.
   * Tells the server what this client supports.
   */
  suspend fun reportSessionCapabilities(
    capabilities: SessionCapabilities,
  ): SdkResult<Unit> = post(
    path = "Sessions/Capabilities/Full",
    body = capabilities,
  )

  /**
   * Logs out the current session.
   * Requires authentication.
   */
  suspend fun logout(): SdkResult<Unit> =
    post<Unit, Unit>(path = "Sessions/Logout")

  /**
   * Initiates Quick Connect.
   * Returns a [QuickConnectResult] with a code to display to the user.
   */
  suspend fun initiateQuickConnect(): SdkResult<QuickConnectResult> =
    get(path = "QuickConnect/Initiate")

  /**
   * Checks the status of a Quick Connect request.
   *
   * @param secret The secret from the [initiateQuickConnect] response.
   */
  suspend fun getQuickConnectStatus(
    secret: String,
  ): SdkResult<QuickConnectResult> = get(
    path = "QuickConnect/Connect",
    queryParams = mapOf("secret" to secret),
  )

  /**
   * Authorizes a Quick Connect code on an authenticated session.
   * Used by the "approving" device.
   *
   * @param code The code displayed on the requesting device.
   */
  suspend fun authorizeQuickConnect(
    code: String,
  ): SdkResult<Unit> = post<Unit, Unit>(
    path = "QuickConnect/Authorize",
    queryParams = mapOf("code" to code),
  )
}
