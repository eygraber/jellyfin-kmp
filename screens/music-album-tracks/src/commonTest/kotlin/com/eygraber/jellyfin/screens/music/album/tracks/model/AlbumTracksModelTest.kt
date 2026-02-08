package com.eygraber.jellyfin.screens.music.album.tracks.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.PaginatedResult
import com.eygraber.jellyfin.data.items.SortOrder
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class AlbumTracksModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var model: AlbumTracksModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    model = AlbumTracksModel(
      itemsRepository = fakeRepository,
    )
  }

  @Test
  fun loadTracks_success_populates_tracks() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "album-1", name = "The Dark Side of the Moon", seriesName = "Pink Floyd"),
      )

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "track-1",
              name = "Speak to Me",
              type = "Audio",
              productionYear = 1,
              runTimeTicks = 9_000_000_000L,
            ),
            createLibraryItem(
              id = "track-2",
              name = "Breathe",
              type = "Audio",
              productionYear = 2,
              runTimeTicks = 16_300_000_000L,
            ),
          ),
          totalRecordCount = 2,
          startIndex = 0,
        ),
      )

      model.loadTracks("album-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
      state.albumName shouldBe "The Dark Side of the Moon"
      state.artistName shouldBe "Pink Floyd"
      state.tracks.size shouldBe 2
      state.tracks[0].name shouldBe "Speak to Me"
      state.tracks[0].trackNumber shouldBe 1
      state.tracks[0].durationText.shouldNotBeNull()
      state.tracks[1].name shouldBe "Breathe"
      state.tracks[1].trackNumber shouldBe 2
    }
  }

  @Test
  fun loadTracks_error_sets_error_state() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "album-1", name = "Album"),
      )

      fakeRepository.getItemsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadTracks("album-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error shouldBe AlbumTracksModelError.LoadFailed
    }
  }

  @Test
  fun track_without_runtime_ticks_has_null_duration() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "album-1", name = "Album"),
      )

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "track-1",
              name = "Track",
              type = "Audio",
              runTimeTicks = null,
            ),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadTracks("album-1")

      val state = model.stateForTest
      state.tracks[0].durationText.shouldBeNull()
    }
  }

  @Test
  fun formatDuration_formats_correctly() {
    AlbumTracksModel.formatDuration(0L) shouldBe "0:00"
    AlbumTracksModel.formatDuration(10_000_000L) shouldBe "0:01"
    AlbumTracksModel.formatDuration(600_000_000L) shouldBe "1:00"
    AlbumTracksModel.formatDuration(1_630_000_000L) shouldBe "2:43"
    AlbumTracksModel.formatDuration(3_600_000_000L) shouldBe "6:00"
  }

  private fun createLibraryItem(
    id: String,
    name: String,
    type: String = "Audio",
    productionYear: Int? = null,
    runTimeTicks: Long? = null,
    seriesName: String? = null,
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = type,
    overview = null,
    productionYear = productionYear,
    communityRating = null,
    officialRating = null,
    primaryImageTag = null,
    backdropImageTags = emptyList(),
    seriesName = seriesName,
    seriesId = null,
    childCount = null,
    runTimeTicks = runTimeTicks,
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
