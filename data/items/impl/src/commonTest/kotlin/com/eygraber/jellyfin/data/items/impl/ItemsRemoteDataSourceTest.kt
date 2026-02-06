package com.eygraber.jellyfin.data.items.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isError
import com.eygraber.jellyfin.common.successOrNull
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.sdk.core.model.ItemsResult
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ItemsRemoteDataSourceTest {
  private lateinit var fakeLibraryService: FakeItemsLibraryService
  private lateinit var dataSource: ItemsRemoteDataSource

  @BeforeTest
  fun setUp() {
    fakeLibraryService = FakeItemsLibraryService()
    dataSource = ItemsRemoteDataSource(libraryService = fakeLibraryService)
  }

  @Test
  fun getItems_maps_dto_to_library_items() {
    runTest {
      fakeLibraryService.itemsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(
              id = "movie-1",
              name = "Inception",
              type = "Movie",
              productionYear = 2010,
              communityRating = 8.8F,
              officialRating = "PG-13",
              overview = "A mind-bending thriller",
              imageTags = mapOf("Primary" to "tag1"),
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      val result = dataSource.getItems(
        parentId = "lib-1",
        includeItemTypes = listOf("Movie"),
        sortBy = ItemSortBy.SortName,
        sortOrder = SortOrder.Ascending,
        startIndex = 0,
        limit = 50,
        genres = null,
        years = null,
        searchTerm = null,
        fields = null,
      )

      val paginatedResult = result.successOrNull.shouldNotBeNull()
      paginatedResult.items.size shouldBe 1
      paginatedResult.totalRecordCount shouldBe 1
      paginatedResult.startIndex shouldBe 0

      val item = paginatedResult.items[0]
      item.id shouldBe "movie-1"
      item.name shouldBe "Inception"
      item.type shouldBe "Movie"
      item.productionYear shouldBe 2010
      item.communityRating shouldBe 8.8F
      item.officialRating shouldBe "PG-13"
      item.overview shouldBe "A mind-bending thriller"
      item.primaryImageTag shouldBe "tag1"
    }
  }

  @Test
  fun getItems_filters_out_items_without_id() {
    runTest {
      fakeLibraryService.itemsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(id = null, name = "No ID"),
            BaseItemDto(id = "valid", name = "Valid Movie", type = "Movie"),
          ),
          totalRecordCount = 2,
        ),
      )

      val result = dataSource.getItems(
        parentId = "lib-1",
        includeItemTypes = null,
        sortBy = ItemSortBy.SortName,
        sortOrder = SortOrder.Ascending,
        startIndex = 0,
        limit = 50,
        genres = null,
        years = null,
        searchTerm = null,
        fields = null,
      )

      val paginatedResult = result.successOrNull.shouldNotBeNull()
      paginatedResult.items.size shouldBe 1
      paginatedResult.items[0].id shouldBe "valid"
    }
  }

  @Test
  fun getItems_propagates_error() {
    runTest {
      fakeLibraryService.itemsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      val result = dataSource.getItems(
        parentId = "lib-1",
        includeItemTypes = null,
        sortBy = ItemSortBy.SortName,
        sortOrder = SortOrder.Ascending,
        startIndex = 0,
        limit = 50,
        genres = null,
        years = null,
        searchTerm = null,
        fields = null,
      )

      result.isError().shouldBeTrue()
    }
  }

  @Test
  fun getItem_maps_dto_to_library_item() {
    runTest {
      fakeLibraryService.singleItemResult = JellyfinResult.Success(
        BaseItemDto(
          id = "movie-1",
          name = "Inception",
          type = "Movie",
          productionYear = 2010,
        ),
      )

      val result = dataSource.getItem(itemId = "movie-1")

      val item = result.successOrNull.shouldNotBeNull()
      item.id shouldBe "movie-1"
      item.name shouldBe "Inception"
    }
  }

  @Test
  fun getSimilarItems_maps_dtos_to_library_items() {
    runTest {
      fakeLibraryService.similarItemsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(id = "sim-1", name = "Similar Movie 1", type = "Movie"),
            BaseItemDto(id = "sim-2", name = "Similar Movie 2", type = "Movie"),
          ),
          totalRecordCount = 2,
        ),
      )

      val result = dataSource.getSimilarItems(
        itemId = "movie-1",
        limit = 10,
      )

      val items = result.successOrNull.shouldNotBeNull()
      items.size shouldBe 2
      items[0].id shouldBe "sim-1"
      items[1].id shouldBe "sim-2"
    }
  }
}

@Suppress("TooManyFunctions")
private class FakeItemsLibraryService : JellyfinLibraryService {
  var itemsResult: JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

  var singleItemResult: JellyfinResult<BaseItemDto> = JellyfinResult.Success(
    BaseItemDto(id = "default"),
  )

  var similarItemsResult: JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

  override suspend fun getResumeItems(
    limit: Int?,
    mediaTypes: List<String>?,
    fields: List<String>?,
  ): JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

  override suspend fun getNextUpEpisodes(
    limit: Int?,
    fields: List<String>?,
  ): JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

  override suspend fun getLatestItems(
    parentId: String?,
    includeItemTypes: List<String>?,
    limit: Int?,
    fields: List<String>?,
  ): JellyfinResult<List<BaseItemDto>> = JellyfinResult.Success(emptyList())

  override suspend fun getUserViews(): JellyfinResult<ItemsResult> = JellyfinResult.Success(
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
  ): JellyfinResult<ItemsResult> = itemsResult

  override suspend fun getItem(itemId: String): JellyfinResult<BaseItemDto> = singleItemResult

  override suspend fun getSimilarItems(
    itemId: String,
    limit: Int?,
  ): JellyfinResult<ItemsResult> = similarItemsResult

  override fun getImageUrl(
    itemId: String,
    imageType: ImageType,
    maxWidth: Int?,
    maxHeight: Int?,
    tag: String?,
    imageIndex: Int?,
  ): String = "https://example.com/images/$itemId/${imageType.apiValue}"
}
