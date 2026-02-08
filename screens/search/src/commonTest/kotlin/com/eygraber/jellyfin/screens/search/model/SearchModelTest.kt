package com.eygraber.jellyfin.screens.search.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.search.SearchRepository
import com.eygraber.jellyfin.data.search.SearchResultItem
import com.eygraber.jellyfin.data.search.SearchResults
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SearchModelTest {
  private lateinit var fakeSearchRepository: FakeSearchRepository
  private lateinit var fakeLibraryService: FakeSearchLibraryService
  private lateinit var model: SearchModel

  @BeforeTest
  fun setUp() {
    fakeSearchRepository = FakeSearchRepository()
    fakeLibraryService = FakeSearchLibraryService()
    model = SearchModel(
      searchRepository = fakeSearchRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun search_maps_movie_results_to_view_items() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Success(
        SearchResults(
          movies = listOf(
            createSearchResultItem(
              id = "m1",
              name = "Inception",
              type = "Movie",
              productionYear = 2010,
              primaryImageTag = "tag1",
            ),
          ),
        ),
      )

      model.search("inception")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
      state.movieResults.size shouldBe 1

      val movie = state.movieResults[0]
      movie.id shouldBe "m1"
      movie.name shouldBe "Inception"
      movie.type shouldBe "Movie"
      movie.year shouldBe "2010"
      movie.imageUrl.shouldNotBeNull()
    }
  }

  @Test
  fun search_maps_series_results_to_view_items() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Success(
        SearchResults(
          series = listOf(
            createSearchResultItem(
              id = "s1",
              name = "Breaking Bad",
              type = "Series",
              productionYear = 2008,
            ),
          ),
        ),
      )

      model.search("breaking")

      val state = model.stateForTest
      state.seriesResults.size shouldBe 1
      state.seriesResults[0].name shouldBe "Breaking Bad"
      state.seriesResults[0].year shouldBe "2008"
    }
  }

  @Test
  fun search_maps_episode_results_with_series_name_as_subtitle() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Success(
        SearchResults(
          episodes = listOf(
            createSearchResultItem(
              id = "e1",
              name = "Ozymandias",
              type = "Episode",
              seriesName = "Breaking Bad",
            ),
          ),
        ),
      )

      model.search("ozymandias")

      val state = model.stateForTest
      state.episodeResults.size shouldBe 1
      state.episodeResults[0].subtitle shouldBe "Breaking Bad"
    }
  }

  @Test
  fun search_maps_music_results() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Success(
        SearchResults(
          music = listOf(
            createSearchResultItem(
              id = "a1",
              name = "Bohemian Rhapsody",
              type = "Audio",
            ),
          ),
        ),
      )

      model.search("bohemian")

      val state = model.stateForTest
      state.musicResults.size shouldBe 1
      state.musicResults[0].name shouldBe "Bohemian Rhapsody"
    }
  }

  @Test
  fun search_maps_people_results() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Success(
        SearchResults(
          people = listOf(
            createSearchResultItem(
              id = "p1",
              name = "Leonardo DiCaprio",
              type = "Person",
            ),
          ),
        ),
      )

      model.search("leonardo")

      val state = model.stateForTest
      state.peopleResults.size shouldBe 1
      state.peopleResults[0].name shouldBe "Leonardo DiCaprio"
    }
  }

  @Test
  fun search_with_blank_query_resets_state() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Success(
        SearchResults(
          movies = listOf(
            createSearchResultItem(id = "m1", name = "Test", type = "Movie"),
          ),
        ),
      )

      model.search("test")
      model.search("   ")

      val state = model.stateForTest
      state.query shouldBe "   "
      state.movieResults.size shouldBe 0
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
    }
  }

  @Test
  fun search_with_error_sets_error_state() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Error(
        message = "Network error",
        isEphemeral = true,
      )

      model.search("inception")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error shouldBe SearchModelError.SearchFailed
      state.query shouldBe "inception"
    }
  }

  @Test
  fun search_with_empty_results_sets_isEmptyResults() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Success(
        SearchResults(),
      )

      model.search("nonexistent")

      val state = model.stateForTest
      state.isEmptyResults.shouldBeTrue()
      state.isLoading.shouldBeFalse()
    }
  }

  @Test
  fun clearSearch_resets_to_initial_state() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Success(
        SearchResults(
          movies = listOf(
            createSearchResultItem(id = "m1", name = "Test", type = "Movie"),
          ),
        ),
      )

      model.search("test")
      model.clearSearch()

      val state = model.stateForTest
      state.query shouldBe ""
      state.movieResults.size shouldBe 0
      state.isLoading.shouldBeFalse()
      state.isEmptyResults.shouldBeFalse()
      state.error.shouldBeNull()
    }
  }

  @Test
  fun item_without_image_tag_has_null_image_url() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Success(
        SearchResults(
          movies = listOf(
            createSearchResultItem(
              id = "m1",
              name = "Test Movie",
              type = "Movie",
              primaryImageTag = null,
            ),
          ),
        ),
      )

      model.search("test")

      val state = model.stateForTest
      state.movieResults[0].imageUrl.shouldBeNull()
    }
  }

  @Test
  fun item_without_production_year_has_null_year() {
    runTest {
      fakeSearchRepository.searchResult = JellyfinResult.Success(
        SearchResults(
          movies = listOf(
            createSearchResultItem(
              id = "m1",
              name = "Test Movie",
              type = "Movie",
              productionYear = null,
            ),
          ),
        ),
      )

      model.search("test")

      val state = model.stateForTest
      state.movieResults[0].year.shouldBeNull()
    }
  }

  private fun createSearchResultItem(
    id: String,
    name: String,
    type: String,
    productionYear: Int? = null,
    primaryImageTag: String? = null,
    seriesName: String? = null,
  ) = SearchResultItem(
    id = id,
    name = name,
    type = type,
    productionYear = productionYear,
    primaryImageTag = primaryImageTag,
    seriesName = seriesName,
    overview = null,
  )
}

private class FakeSearchRepository : SearchRepository {
  var searchResult: JellyfinResult<SearchResults> = JellyfinResult.Success(SearchResults())
  var searchByTypeResult: JellyfinResult<List<SearchResultItem>> = JellyfinResult.Success(emptyList())

  override suspend fun search(
    query: String,
    limit: Int,
  ): JellyfinResult<SearchResults> = searchResult

  override suspend fun searchByType(
    query: String,
    itemType: String,
    limit: Int,
  ): JellyfinResult<List<SearchResultItem>> = searchByTypeResult
}

private class FakeSearchLibraryService : JellyfinLibraryService {
  override fun getImageUrl(
    itemId: String,
    imageType: ImageType,
    maxWidth: Int?,
    maxHeight: Int?,
    tag: String?,
    imageIndex: Int?,
  ): String = "https://example.com/images/$itemId/${imageType.apiValue}"

  override suspend fun getResumeItems(
    limit: Int?,
    mediaTypes: List<String>?,
    fields: List<String>?,
  ) = error("Not used in tests")

  override suspend fun getLatestItems(
    parentId: String?,
    includeItemTypes: List<String>?,
    limit: Int?,
    fields: List<String>?,
  ) = error("Not used in tests")

  override suspend fun getNextUpEpisodes(
    limit: Int?,
    fields: List<String>?,
  ) = error("Not used in tests")

  override suspend fun getUserViews() = error("Not used in tests")

  @Suppress("LongParameterList")
  override suspend fun getItems(
    parentId: String?,
    includeItemTypes: List<String>?,
    sortBy: List<String>?,
    sortOrder: String?,
    startIndex: Int?,
    limit: Int?,
    recursive: Boolean?,
    genres: List<String>?,
    years: List<Int>?,
    searchTerm: String?,
    fields: List<String>?,
  ) = error("Not used in tests")

  override suspend fun getItem(itemId: String) = error("Not used in tests")

  override suspend fun getSimilarItems(
    itemId: String,
    limit: Int?,
  ) = error("Not used in tests")
}
