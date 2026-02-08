package com.eygraber.jellyfin.data.search.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.sdk.core.model.ItemsResult
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DefaultSearchRepositoryTest {
  private lateinit var fakeLibraryService: FakeSearchLibraryService
  private lateinit var repository: DefaultSearchRepository

  @BeforeTest
  fun setUp() {
    fakeLibraryService = FakeSearchLibraryService()
    repository = DefaultSearchRepository(
      remoteDataSource = SearchRemoteDataSource(fakeLibraryService),
    )
  }

  @Test
  fun search_returns_grouped_results() {
    runTest {
      fakeLibraryService.searchResults = mapOf(
        listOf("Movie") to JellyfinResult.Success(
          ItemsResult(
            items = listOf(
              createBaseItemDto(id = "m1", name = "Inception", type = "Movie"),
            ),
            totalRecordCount = 1,
          ),
        ),
        listOf("Series") to JellyfinResult.Success(
          ItemsResult(
            items = listOf(
              createBaseItemDto(id = "s1", name = "Breaking Bad", type = "Series"),
            ),
            totalRecordCount = 1,
          ),
        ),
      )

      val result = repository.search(query = "test", limit = 10)

      result.isSuccess().shouldBeTrue()
      val searchResults = (result as JellyfinResult.Success).value
      searchResults.movies shouldHaveSize 1
      searchResults.movies[0].name shouldBe "Inception"
      searchResults.series shouldHaveSize 1
      searchResults.series[0].name shouldBe "Breaking Bad"
    }
  }

  @Test
  fun search_returns_empty_results_when_no_matches() {
    runTest {
      val result = repository.search(query = "nonexistent", limit = 10)

      result.isSuccess().shouldBeTrue()
      val searchResults = (result as JellyfinResult.Success).value
      searchResults.isEmpty.shouldBeTrue()
      searchResults.totalCount shouldBe 0
    }
  }

  @Test
  fun search_partial_failure_returns_successful_results() {
    runTest {
      fakeLibraryService.searchResults = mapOf(
        listOf("Movie") to JellyfinResult.Success(
          ItemsResult(
            items = listOf(
              createBaseItemDto(id = "m1", name = "Inception", type = "Movie"),
            ),
            totalRecordCount = 1,
          ),
        ),
        listOf("Series") to JellyfinResult.Error(
          message = "Network error",
          isEphemeral = true,
        ),
      )

      val result = repository.search(query = "test", limit = 10)

      result.isSuccess().shouldBeTrue()
      val searchResults = (result as JellyfinResult.Success).value
      searchResults.movies shouldHaveSize 1
      searchResults.series.shouldBeEmpty()
    }
  }

  @Test
  fun search_all_failures_returns_error() {
    runTest {
      fakeLibraryService.defaultResult = JellyfinResult.Error(
        message = "Network error",
        isEphemeral = true,
      )

      val result = repository.search(query = "test", limit = 10)

      result.isSuccess().shouldBeFalse()
    }
  }

  @Test
  fun searchByType_returns_items() {
    runTest {
      fakeLibraryService.searchResults = mapOf(
        listOf("Movie") to JellyfinResult.Success(
          ItemsResult(
            items = listOf(
              createBaseItemDto(id = "m1", name = "Inception", type = "Movie"),
              createBaseItemDto(id = "m2", name = "Interstellar", type = "Movie"),
            ),
            totalRecordCount = 2,
          ),
        ),
      )

      val result = repository.searchByType(
        query = "test",
        itemType = "Movie",
        limit = 10,
      )

      result.isSuccess().shouldBeTrue()
      val items = (result as JellyfinResult.Success).value
      items shouldHaveSize 2
      items[0].name shouldBe "Inception"
      items[1].name shouldBe "Interstellar"
    }
  }

  @Test
  fun search_result_item_maps_fields_correctly() {
    runTest {
      fakeLibraryService.searchResults = mapOf(
        listOf("Movie") to JellyfinResult.Success(
          ItemsResult(
            items = listOf(
              createBaseItemDto(
                id = "m1",
                name = "Inception",
                type = "Movie",
                productionYear = 2010,
                overview = "A mind-bending thriller",
                seriesName = null,
              ),
            ),
            totalRecordCount = 1,
          ),
        ),
      )

      val result = repository.search(query = "inception", limit = 10)
      val movie = (result as JellyfinResult.Success).value.movies[0]

      movie.id shouldBe "m1"
      movie.name shouldBe "Inception"
      movie.type shouldBe "Movie"
      movie.productionYear shouldBe 2010
      movie.overview shouldBe "A mind-bending thriller"
    }
  }

  private fun createBaseItemDto(
    id: String,
    name: String,
    type: String,
    productionYear: Int? = null,
    overview: String? = null,
    seriesName: String? = null,
  ) = BaseItemDto(
    id = id,
    name = name,
    type = type,
    productionYear = productionYear,
    overview = overview,
    seriesName = seriesName,
    imageTags = emptyMap(),
    backdropImageTags = emptyList(),
    people = emptyList(),
  )
}

private class FakeSearchLibraryService : JellyfinLibraryService {
  var searchResults: Map<List<String>, JellyfinResult<ItemsResult>> = emptyMap()
  var defaultResult: JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

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
  ): JellyfinResult<ItemsResult> =
    searchResults[includeItemTypes] ?: defaultResult

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

  override suspend fun getItem(itemId: String) = error("Not used in tests")

  override suspend fun getSimilarItems(
    itemId: String,
    limit: Int?,
  ) = error("Not used in tests")
}
