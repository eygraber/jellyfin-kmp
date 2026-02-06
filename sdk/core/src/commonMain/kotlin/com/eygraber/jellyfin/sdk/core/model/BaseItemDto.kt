@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseItemDto(
  @SerialName("Name") val name: String? = null,
  @SerialName("OriginalTitle") val originalTitle: String? = null,
  @SerialName("ServerId") val serverId: String? = null,
  @SerialName("Id") val id: String? = null,
  @SerialName("Etag") val etag: String? = null,
  @SerialName("SourceType") val sourceType: String? = null,
  @SerialName("PlaylistItemId") val playlistItemId: String? = null,
  @SerialName("DateCreated") val dateCreated: String? = null,
  @SerialName("DateLastMediaAdded") val dateLastMediaAdded: String? = null,
  @SerialName("ExtraType") val extraType: String? = null,
  @SerialName("SortName") val sortName: String? = null,
  @SerialName("PremiereDate") val premiereDate: String? = null,
  @SerialName("Overview") val overview: String? = null,
  @SerialName("ParentId") val parentId: String? = null,
  @SerialName("Path") val path: String? = null,
  @SerialName("OfficialRating") val officialRating: String? = null,
  @SerialName("CommunityRating") val communityRating: Float? = null,
  @SerialName("CriticRating") val criticRating: Float? = null,
  @SerialName("RunTimeTicks") val runTimeTicks: Long? = null,
  @SerialName("ProductionYear") val productionYear: Int? = null,
  @SerialName("IndexNumber") val indexNumber: Int? = null,
  @SerialName("ParentIndexNumber") val parentIndexNumber: Int? = null,
  @SerialName("IsFolder") val isFolder: Boolean? = null,
  @SerialName("Type") val type: String? = null,
  @SerialName("CollectionType") val collectionType: String? = null,
  @SerialName("SeriesId") val seriesId: String? = null,
  @SerialName("SeriesName") val seriesName: String? = null,
  @SerialName("SeasonId") val seasonId: String? = null,
  @SerialName("SeasonName") val seasonName: String? = null,
  @SerialName("Genres") val genres: List<String> = emptyList(),
  @SerialName("Tags") val tags: List<String> = emptyList(),
  @SerialName("Studios") val studios: List<NameIdPair> = emptyList(),
  @SerialName("People") val people: List<BaseItemPerson> = emptyList(),
  @SerialName("ImageTags") val imageTags: Map<String, String> = emptyMap(),
  @SerialName("BackdropImageTags") val backdropImageTags: List<String> = emptyList(),
  @SerialName("ParentBackdropImageTags") val parentBackdropImageTags: List<String> = emptyList(),
  @SerialName("ParentBackdropItemId") val parentBackdropItemId: String? = null,
  @SerialName("UserData") val userData: UserItemDataDto? = null,
  @SerialName("ChildCount") val childCount: Int? = null,
  @SerialName("MediaType") val mediaType: String? = null,
  @SerialName("Status") val status: String? = null,
  @SerialName("AirDays") val airDays: List<String> = emptyList(),
  @SerialName("EndDate") val endDate: String? = null,
  @SerialName("AlbumArtist") val albumArtist: String? = null,
  @SerialName("AlbumId") val albumId: String? = null,
  @SerialName("ArtistItems") val artistItems: List<NameIdPair> = emptyList(),
)

@Serializable
data class NameIdPair(
  @SerialName("Name") val name: String? = null,
  @SerialName("Id") val id: String? = null,
)

@Serializable
data class BaseItemPerson(
  @SerialName("Name") val name: String? = null,
  @SerialName("Id") val id: String? = null,
  @SerialName("Role") val role: String? = null,
  @SerialName("Type") val type: String? = null,
  @SerialName("PrimaryImageTag") val primaryImageTag: String? = null,
)

@Serializable
data class UserItemDataDto(
  @SerialName("Rating") val rating: Double? = null,
  @SerialName("PlayedPercentage") val playedPercentage: Double? = null,
  @SerialName("UnplayedItemCount") val unplayedItemCount: Int? = null,
  @SerialName("PlaybackPositionTicks") val playbackPositionTicks: Long = 0,
  @SerialName("PlayCount") val playCount: Int = 0,
  @SerialName("IsFavorite") val isFavorite: Boolean = false,
  @SerialName("Played") val played: Boolean = false,
  @SerialName("Key") val key: String? = null,
  @SerialName("ItemId") val itemId: String? = null,
)
