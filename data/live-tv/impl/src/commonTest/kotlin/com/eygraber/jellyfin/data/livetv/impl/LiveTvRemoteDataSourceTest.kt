package com.eygraber.jellyfin.data.livetv.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isError
import com.eygraber.jellyfin.common.successOrNull
import com.eygraber.jellyfin.data.livetv.RecordingStatus
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvChannelResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvGuideInfo
import com.eygraber.jellyfin.sdk.core.model.LiveTvProgramResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvRecordingResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvTimerInfoDto
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LiveTvRemoteDataSourceTest {
  private lateinit var fakeService: FakeJellyfinLiveTvService
  private lateinit var dataSource: LiveTvRemoteDataSource

  @BeforeTest
  fun setUp() {
    fakeService = FakeJellyfinLiveTvService()
    dataSource = LiveTvRemoteDataSource(liveTvService = fakeService)
  }

  @Test
  fun getChannels_maps_dto_to_tv_channels() {
    runTest {
      fakeService.channelsResult = JellyfinResult.Success(
        LiveTvChannelResult(
          items = listOf(
            BaseItemDto(
              id = "channel-1",
              name = "BBC One",
              type = "TvChannel",
              channelType = "Tv",
              channelNumber = "1",
              imageTags = mapOf("Primary" to "tag1"),
            ),
            BaseItemDto(id = null, name = "Missing ID"),
          ),
          totalRecordCount = 2,
        ),
      )

      val result = dataSource.getChannels(
        startIndex = 0,
        limit = 50,
        channelType = null,
        isFavorite = null,
      )

      val page = result.successOrNull.shouldNotBeNull()
      page.items.size shouldBe 1
      page.totalRecordCount shouldBe 2
      page.startIndex shouldBe 0

      val channel = page.items[0]
      channel.id shouldBe "channel-1"
      channel.name shouldBe "BBC One"
      channel.number shouldBe "1"
      channel.type shouldBe "Tv"
      channel.primaryImageTag shouldBe "tag1"
    }
  }

  @Test
  fun searchChannels_passes_search_term() {
    runTest {
      fakeService.channelsResult = JellyfinResult.Success(
        LiveTvChannelResult(
          items = listOf(
            BaseItemDto(id = "ch-1", name = "BBC News"),
          ),
          totalRecordCount = 1,
        ),
      )

      val result = dataSource.searchChannels(searchTerm = "news", limit = 10)

      val page = result.successOrNull.shouldNotBeNull()
      page.items.size shouldBe 1
      fakeService.lastChannelsSearchTerm shouldBe "news"
    }
  }

  @Test
  fun getChannels_propagates_error() {
    runTest {
      fakeService.channelsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      val result = dataSource.getChannels(
        startIndex = 0,
        limit = 50,
        channelType = null,
        isFavorite = null,
      )

      result.isError().shouldBeTrue()
    }
  }

  @Test
  fun getCurrentPrograms_filters_to_airing() {
    runTest {
      fakeService.programsResult = JellyfinResult.Success(
        LiveTvProgramResult(
          items = listOf(
            BaseItemDto(
              id = "prog-1",
              channelId = "ch-1",
              channelName = "BBC One",
              name = "Evening News",
              startDate = "2024-01-01T18:00:00Z",
              endDate = "2024-01-01T19:00:00Z",
            ),
            BaseItemDto(id = "prog-2", name = "Missing channel"),
          ),
          totalRecordCount = 2,
        ),
      )

      val result = dataSource.getCurrentPrograms(channelIds = listOf("ch-1"))

      val programs = result.successOrNull.shouldNotBeNull()
      programs.size shouldBe 1
      programs[0].id shouldBe "prog-1"
      programs[0].channelId shouldBe "ch-1"
      programs[0].channelName shouldBe "BBC One"
      programs[0].startDate shouldBe "2024-01-01T18:00:00Z"
      programs[0].endDate shouldBe "2024-01-01T19:00:00Z"

      fakeService.lastProgramsIsAiring shouldBe true
      fakeService.lastProgramsChannelIds shouldBe listOf("ch-1")
    }
  }

  @Test
  fun getUpcomingPrograms_passes_has_aired_false() {
    runTest {
      fakeService.programsResult = JellyfinResult.Success(
        LiveTvProgramResult(items = emptyList(), totalRecordCount = 0),
      )

      dataSource.getUpcomingPrograms(channelIds = null, limit = 5)

      fakeService.lastProgramsHasAired shouldBe false
      fakeService.lastProgramsLimit shouldBe 5
    }
  }

  @Test
  fun getPrograms_passes_date_range() {
    runTest {
      fakeService.programsResult = JellyfinResult.Success(
        LiveTvProgramResult(items = emptyList(), totalRecordCount = 0),
      )

      dataSource.getPrograms(
        channelIds = null,
        minStartDate = "2024-01-01T00:00:00Z",
        maxStartDate = "2024-01-02T00:00:00Z",
        limit = 100,
      )

      fakeService.lastProgramsMinStartDate shouldBe "2024-01-01T00:00:00Z"
      fakeService.lastProgramsMaxStartDate shouldBe "2024-01-02T00:00:00Z"
      fakeService.lastProgramsLimit shouldBe 100
    }
  }

  @Test
  fun getProgram_maps_single_program() {
    runTest {
      fakeService.singleProgramResult = JellyfinResult.Success(
        BaseItemDto(
          id = "prog-1",
          channelId = "ch-1",
          name = "News at Ten",
          startDate = "2024-01-01T22:00:00Z",
        ),
      )

      val result = dataSource.getProgram(programId = "prog-1")

      val program = result.successOrNull.shouldNotBeNull()
      program.id shouldBe "prog-1"
      program.channelId shouldBe "ch-1"
      program.name shouldBe "News at Ten"
    }
  }

  @Test
  fun getRecordings_maps_dto_to_tv_recordings() {
    runTest {
      fakeService.recordingsResult = JellyfinResult.Success(
        LiveTvRecordingResult(
          items = listOf(
            BaseItemDto(
              id = "rec-1",
              name = "Recorded Show",
              channelId = "ch-1",
              channelName = "BBC One",
              programId = "prog-1",
              status = "Completed",
              path = "/recordings/rec-1.ts",
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      val result = dataSource.getRecordings(
        startIndex = 0,
        limit = 50,
        status = RecordingStatus.Completed,
      )

      val page = result.successOrNull.shouldNotBeNull()
      page.items.size shouldBe 1

      val recording = page.items[0]
      recording.id shouldBe "rec-1"
      recording.channelId shouldBe "ch-1"
      recording.programId shouldBe "prog-1"
      recording.status shouldBe RecordingStatus.Completed
      recording.path shouldBe "/recordings/rec-1.ts"

      fakeService.lastRecordingsStatus shouldBe "Completed"
    }
  }

  @Test
  fun getRecordings_does_not_pass_unknown_status() {
    runTest {
      fakeService.recordingsResult = JellyfinResult.Success(
        LiveTvRecordingResult(items = emptyList(), totalRecordCount = 0),
      )

      dataSource.getRecordings(
        startIndex = 0,
        limit = 50,
        status = RecordingStatus.Unknown,
      )

      fakeService.lastRecordingsStatus shouldBe null
    }
  }

  @Test
  fun deleteRecording_delegates_to_service() {
    runTest {
      val result = dataSource.deleteRecording(recordingId = "rec-1")

      result.successOrNull.shouldNotBeNull()
      fakeService.lastDeletedRecordingId shouldBe "rec-1"
    }
  }

  @Test
  fun getTimers_maps_timer_dtos() {
    runTest {
      fakeService.timersResult = JellyfinResult.Success(
        listOf(
          LiveTvTimerInfoDto(
            id = "timer-1",
            channelId = "ch-1",
            channelName = "BBC One",
            programId = "prog-1",
            name = "News",
            startDate = "2024-01-01T18:00:00Z",
            endDate = "2024-01-01T19:00:00Z",
            status = "InProgress",
            isPrePaddingRequired = true,
            prePaddingSeconds = 30,
          ),
          LiveTvTimerInfoDto(id = null, name = "missing-id"),
        ),
      )

      val result = dataSource.getTimers(channelId = "ch-1")

      val timers = result.successOrNull.shouldNotBeNull()
      timers.size shouldBe 1
      val timer = timers[0]
      timer.id shouldBe "timer-1"
      timer.channelId shouldBe "ch-1"
      timer.programId shouldBe "prog-1"
      timer.status shouldBe RecordingStatus.InProgress
      timer.isPrePaddingRequired.shouldBeTrue()
      timer.prePaddingSeconds shouldBe 30

      fakeService.lastTimersChannelId shouldBe "ch-1"
    }
  }

  @Test
  fun createTimer_sends_program_id_and_padding() {
    runTest {
      dataSource.createTimer(
        programId = "prog-1",
        prePaddingSeconds = 60,
        postPaddingSeconds = 120,
      )

      val sent = fakeService.lastCreatedTimer.shouldNotBeNull()
      sent.programId shouldBe "prog-1"
      sent.isPrePaddingRequired.shouldBeTrue()
      sent.isPostPaddingRequired.shouldBeTrue()
      sent.prePaddingSeconds shouldBe 60
      sent.postPaddingSeconds shouldBe 120
    }
  }

  @Test
  fun createTimer_without_padding_sends_zero_and_false() {
    runTest {
      dataSource.createTimer(
        programId = "prog-1",
        prePaddingSeconds = null,
        postPaddingSeconds = null,
      )

      val sent = fakeService.lastCreatedTimer.shouldNotBeNull()
      sent.programId shouldBe "prog-1"
      sent.isPrePaddingRequired shouldBe false
      sent.isPostPaddingRequired shouldBe false
      sent.prePaddingSeconds shouldBe 0
      sent.postPaddingSeconds shouldBe 0
    }
  }

  @Test
  fun cancelTimer_delegates_to_service() {
    runTest {
      dataSource.cancelTimer(timerId = "timer-1")

      fakeService.lastCancelledTimerId shouldBe "timer-1"
    }
  }

  @Test
  fun getSeriesTimers_maps_dtos() {
    runTest {
      fakeService.seriesTimersResult = JellyfinResult.Success(
        LiveTvSeriesTimerInfoResult(
          items = listOf(
            LiveTvSeriesTimerInfoDto(
              id = "series-1",
              channelId = "ch-1",
              name = "Doctor Who",
              recordAnyChannel = true,
              recordNewOnly = true,
              days = listOf("Saturday"),
              keepUpTo = 5,
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      val result = dataSource.getSeriesTimers()

      val timers = result.successOrNull.shouldNotBeNull()
      timers.size shouldBe 1
      val timer = timers[0]
      timer.id shouldBe "series-1"
      timer.recordAnyChannel.shouldBeTrue()
      timer.recordNewOnly.shouldBeTrue()
      timer.days shouldBe listOf("Saturday")
      timer.keepUpTo shouldBe 5
    }
  }

  @Test
  fun createSeriesTimer_fetches_defaults_then_overrides_rules() {
    runTest {
      fakeService.defaultSeriesTimerResult = JellyfinResult.Success(
        LiveTvSeriesTimerInfoDto(
          id = "default-id",
          name = "Doctor Who",
          channelId = "ch-1",
          channelName = "BBC One",
          recordAnyChannel = false,
          recordAnyTime = true,
          recordNewOnly = false,
        ),
      )

      dataSource.createSeriesTimer(
        programId = "prog-1",
        recordAnyChannel = true,
        recordAnyTime = false,
        recordNewOnly = true,
      )

      fakeService.lastDefaultSeriesTimerProgramId shouldBe "prog-1"

      val sent = fakeService.lastCreatedSeriesTimer.shouldNotBeNull()
      // Server-populated fields are preserved.
      sent.name shouldBe "Doctor Who"
      sent.channelId shouldBe "ch-1"
      sent.channelName shouldBe "BBC One"
      // Caller's rule overrides are applied.
      sent.recordAnyChannel.shouldBeTrue()
      sent.recordAnyTime shouldBe false
      sent.recordNewOnly.shouldBeTrue()
    }
  }

  @Test
  fun createSeriesTimer_propagates_defaults_failure() {
    runTest {
      fakeService.defaultSeriesTimerResult = JellyfinResult.Error(
        message = "Program not found",
        isEphemeral = false,
      )

      val result = dataSource.createSeriesTimer(
        programId = "missing",
        recordAnyChannel = false,
        recordAnyTime = false,
        recordNewOnly = false,
      )

      result.isError().shouldBeTrue()
      // Should not have attempted to create when defaults fetch failed.
      fakeService.lastCreatedSeriesTimer shouldBe null
    }
  }

  @Test
  fun cancelSeriesTimer_delegates_to_service() {
    runTest {
      dataSource.cancelSeriesTimer(timerId = "series-1")

      fakeService.lastCancelledSeriesTimerId shouldBe "series-1"
    }
  }

  @Test
  fun getGuideInfo_maps_to_domain_type() {
    runTest {
      fakeService.guideInfoResult = JellyfinResult.Success(
        LiveTvGuideInfo(
          startDate = "2024-01-01T00:00:00Z",
          endDate = "2024-01-15T00:00:00Z",
        ),
      )

      val result = dataSource.getGuideInfo()

      val info = result.successOrNull.shouldNotBeNull()
      info.startDate shouldBe "2024-01-01T00:00:00Z"
      info.endDate shouldBe "2024-01-15T00:00:00Z"
    }
  }
}
