package com.eygraber.jellyfin.data.admin

/**
 * A virtual folder ("library") configured on the server.
 *
 * Surfaced by [LibraryAdminRepository.getLibraries] for admin display and
 * scan/refresh operations.
 */
data class ManagedLibrary(
  val itemId: String?,
  val name: String,
  val collectionType: String?,
  val locations: List<String>,
  /**
   * Refresh progress in `0.0..100.0`, or null if no refresh is in progress.
   */
  val refreshProgressPercent: Double?,
  val refreshStatus: String?,
  val primaryImageItemId: String?,
)
