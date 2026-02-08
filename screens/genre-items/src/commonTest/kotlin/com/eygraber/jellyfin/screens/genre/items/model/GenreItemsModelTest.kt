package com.eygraber.jellyfin.screens.genre.items.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.PaginatedResult
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class GenreItemsModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeGenreItemsLibraryService
  private lateinit var model: GenreItemsModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeGenreItemsLibraryService()
    model = GenreItemsModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadInitial_success_populates_items() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "movie-1",
              name = "Blade Runner 2049",
              productionYear = 2017,
              primaryImageTag = "tag1",
            ),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadInitial(libraryId = "lib-1", genreName = "Science Fiction")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
      state.genreName shouldBe "Science Fiction"
      state.items.size shouldBe 1
      state.items[0].name shouldBe "Blade Runner 2049"
      state.items[0].productionYear shouldBe 2017
    }
  }

  @Test
  fun loadInitial_error_sets_error_state() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadInitial(libraryId = "lib-1", genreName = "Action")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error shouldBe GenreItemsModelError.LoadFailed
    }
  }

  @Test
  fun item_without_image_tag_has_null_image_url() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "movie-1",
              name = "Movie",
              primaryImageTag = null,
            ),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadInitial(libraryId = "lib-1", genreName = "Drama")

      val state = model.stateForTest
      state.items[0].imageUrl.shouldBeNull()
    }
  }

  private fun createLibraryItem(
    id: String,
    name: String,
    productionYear: Int? = null,
    primaryImageTag: String? = null,
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = "Movie",
    overview = null,
    productionYear = productionYear,
    communityRating = null,
    officialRating = null,
    primaryImageTag = primaryImageTag,
    backdropImageTags = emptyList(),
    seriesName = null,
    seriesId = null,
    childCount = null,
    runTimeTicks = null,
  )
}

@Suppress("LongParameterList")
private class FakeItemsRepository : ItemsRepository {
  var getItemsResult: JellyfinResult<PaginatedResult<LibraryItem>> = JellyfinResult.Success(
    PaginatedResult(items = emptyList(), totalRecordCount = 0, startIndex = 0),
  )

  var getItemResult: JellyfinResult<LibraryItem> = JellyfinResult.Error(
    message = "Not configured",
    isEphemeral = true,
  )

  var getSimilarItemsResult: JellyfinResult<List<LibraryItem>> = JellyfinResult.Success(emptyList())

  override suspend fun getItems(
    parentId: String,
    includeItemTypes: List<String>?,
    sortBy: ItemSortBy,
    sortOrder: SortOrder,
    startIndex: Int,
    limit: Int,
    genres: List<String>?,
    years: List<Int>?,
    searchTerm: String?,
    fields: List<String>?,
  ): JellyfinResult<PaginatedResult<LibraryItem>> = getItemsResult

  override suspend fun getItem(itemId: String): JellyfinResult<LibraryItem> = getItemResult

  override suspend fun getSimilarItems(
    itemId: String,
    limit: Int?,
  ): JellyfinResult<List<LibraryItem>> = getSimilarItemsResult
}

private class FakeGenreItemsLibraryService : JellyfinLibraryService {
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
