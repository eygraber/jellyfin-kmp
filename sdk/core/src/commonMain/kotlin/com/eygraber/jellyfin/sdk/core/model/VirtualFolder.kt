@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A virtual folder ("library") configured on the Jellyfin server.
 *
 * Returned by `GET /Library/VirtualFolders`.
 */
@Serializable
data class VirtualFolderInfo(
  @SerialName("Name") val name: String? = null,
  @SerialName("Locations") val locations: List<String> = emptyList(),
  @SerialName("CollectionType") val collectionType: String? = null,
  @SerialName("ItemId") val itemId: String? = null,
  @SerialName("PrimaryImageItemId") val primaryImageItemId: String? = null,
  @SerialName("RefreshProgress") val refreshProgress: Double? = null,
  @SerialName("RefreshStatus") val refreshStatus: String? = null,
)
