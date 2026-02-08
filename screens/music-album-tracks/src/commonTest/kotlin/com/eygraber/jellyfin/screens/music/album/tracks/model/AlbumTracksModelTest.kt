package com.eygraber.jellyfin.screens.music.album.tracks.model

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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class AlbumTracksModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeAlbumTracksLibraryService
  private lateinit var model: AlbumTracksModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeAlbumTracksLibraryService()
    model = AlbumTracksModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadTracks_success_populates_album_detail_and_tracks() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "album-1",
          name = "The Dark Side of the Moon",
          seriesName = "Pink Floyd",
          seriesId = "artist-1",
          primaryImageTag = "tag1",
          officialRating = "Progressive Rock",
          productionYear = 1973,
        ),
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

      val album = state.album
      album.shouldNotBeNull()
      album.name shouldBe "The Dark Side of the Moon"
      album.artistName shouldBe "Pink Floyd"
      album.artistId shouldBe "artist-1"
      album.productionYear shouldBe 1973
      album.genre shouldBe "Progressive Rock"
      album.albumArtUrl.shouldNotBeNull()

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
  fun album_without_image_tag_has_null_art_url() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "album-1", name = "Album", primaryImageTag = null),
      )

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(items = emptyList(), totalRecordCount = 0, startIndex = 0),
      )

      model.loadTracks("album-1")

      val state = model.stateForTest
      val album = state.album
      album.shouldNotBeNull()
      album.albumArtUrl.shouldBeNull()
    }
  }

  @Test
  fun currentAlbumArtistId_returns_artist_id_after_load() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "album-1", name = "Album", seriesId = "artist-42"),
      )

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(items = emptyList(), totalRecordCount = 0, startIndex = 0),
      )

      model.loadTracks("album-1")

      model.currentAlbumArtistId() shouldBe "artist-42"
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

  @Suppress("LongParameterList")
  private fun createLibraryItem(
    id: String,
    name: String,
    type: String = "Audio",
    productionYear: Int? = null,
    runTimeTicks: Long? = null,
    seriesName: String? = null,
    seriesId: String? = null,
    primaryImageTag: String? = null,
    officialRating: String? = null,
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = type,
    overview = null,
    productionYear = productionYear,
    communityRating = null,
    officialRating = officialRating,
    primaryImageTag = primaryImageTag,
    backdropImageTags = emptyList(),
    seriesName = seriesName,
    seriesId = seriesId,
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

private class FakeAlbumTracksLibraryService : JellyfinLibraryService {
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
