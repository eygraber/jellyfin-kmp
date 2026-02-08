package com.eygraber.jellyfin.data.items

/**
 * Domain model representing a media item in a library.
 *
 * This is a unified model that can represent movies, series, albums,
 * and other library content types.
 */
data class LibraryItem(
  val id: String,
  val name: String,
  val sortName: String?,
  val type: String,
  val overview: String?,
  val productionYear: Int?,
  val communityRating: Float?,
  val officialRating: String?,
  val primaryImageTag: String?,
  val backdropImageTags: List<String>,
  val seriesName: String?,
  val seriesId: String?,
  val childCount: Int?,
  val runTimeTicks: Long?,
  val people: List<PersonItem> = emptyList(),
)

/**
 * A person associated with a media item (cast member, crew, etc.).
 */
data class PersonItem(
  val id: String,
  val name: String,
  val role: String?,
  val type: String?,
  val primaryImageTag: String?,
)
