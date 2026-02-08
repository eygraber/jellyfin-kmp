package com.eygraber.jellyfin.data.search.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.search.SearchRepository
import com.eygraber.jellyfin.data.search.SearchResultItem
import com.eygraber.jellyfin.data.search.SearchResults
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [SearchRepository].
 *
 * Performs parallel searches per item type and groups results.
 * This repository is stateless per project conventions.
 */
@ContributesBinding(AppScope::class)
class DefaultSearchRepository(
  private val remoteDataSource: SearchRemoteDataSource,
) : SearchRepository {
  override suspend fun search(
    query: String,
    limit: Int,
  ): JellyfinResult<SearchResults> {
    val moviesResult = remoteDataSource.search(
      query = query,
      includeItemTypes = listOf(ITEM_TYPE_MOVIE),
      limit = limit,
    )

    val seriesResult = remoteDataSource.search(
      query = query,
      includeItemTypes = listOf(ITEM_TYPE_SERIES),
      limit = limit,
    )

    val episodesResult = remoteDataSource.search(
      query = query,
      includeItemTypes = listOf(ITEM_TYPE_EPISODE),
      limit = limit,
    )

    val musicResult = remoteDataSource.search(
      query = query,
      includeItemTypes = listOf(ITEM_TYPE_AUDIO, ITEM_TYPE_MUSIC_ALBUM),
      limit = limit,
    )

    val peopleResult = remoteDataSource.search(
      query = query,
      includeItemTypes = listOf(ITEM_TYPE_PERSON),
      limit = limit,
    )

    val allResults = listOf(moviesResult, seriesResult, episodesResult, musicResult, peopleResult)

    if(allResults.none { it.isSuccess() }) {
      return JellyfinResult.Error(
        message = "Search failed",
        isEphemeral = true,
      )
    }

    return JellyfinResult.Success(
      SearchResults(
        movies = moviesResult.successOrEmpty(),
        series = seriesResult.successOrEmpty(),
        episodes = episodesResult.successOrEmpty(),
        music = musicResult.successOrEmpty(),
        people = peopleResult.successOrEmpty(),
      ),
    )
  }

  override suspend fun searchByType(
    query: String,
    itemType: String,
    limit: Int,
  ): JellyfinResult<List<SearchResultItem>> =
    remoteDataSource.search(
      query = query,
      includeItemTypes = listOf(itemType),
      limit = limit,
    )

  companion object {
    internal const val ITEM_TYPE_MOVIE = "Movie"
    internal const val ITEM_TYPE_SERIES = "Series"
    internal const val ITEM_TYPE_EPISODE = "Episode"
    internal const val ITEM_TYPE_AUDIO = "Audio"
    internal const val ITEM_TYPE_MUSIC_ALBUM = "MusicAlbum"
    internal const val ITEM_TYPE_PERSON = "Person"
  }
}

private fun <T> JellyfinResult<List<T>>.successOrEmpty(): List<T> = when(this) {
  is JellyfinResult.Success -> value
  is JellyfinResult.Error -> emptyList()
}
