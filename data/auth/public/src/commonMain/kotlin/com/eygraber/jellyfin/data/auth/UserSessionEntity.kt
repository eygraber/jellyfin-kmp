package com.eygraber.jellyfin.data.auth

/**
 * Domain model for a persisted user session.
 *
 * Represents an authenticated session linking a user to a server.
 * Multiple sessions can exist per server (different users), but only
 * one session can be active at a time.
 */
data class UserSessionEntity(
  val id: String,
  val serverId: String,
  val userId: String,
  val username: String,
  val accessToken: String,
  val isActive: Boolean,
  val createdAt: Long,
  val lastUsedAt: Long,
)
