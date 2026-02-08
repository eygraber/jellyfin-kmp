package com.eygraber.jellyfin.data.playback.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.playback.PlayMethod
import com.eygraber.jellyfin.sdk.core.model.MediaSourceInfo
import com.eygraber.jellyfin.sdk.core.model.MediaStream
import com.eygraber.jellyfin.sdk.core.model.PlaybackInfoResponse
import com.eygraber.jellyfin.sdk.core.model.PlaybackProgressInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStartInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStopInfo
import com.eygraber.jellyfin.services.sdk.JellyfinPlaybackService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PlaybackRemoteDataSourceTest {
  private val fakeService = FakeJellyfinPlaybackService()
  private val dataSource = PlaybackRemoteDataSource(playbackService = fakeService)

  @Test
  fun get_playback_session_returns_direct_play_when_supported() {
    runTest {
      fakeService.playbackInfoResult = JellyfinResult.Success(
        createPlaybackInfoResponse(
          supportsDirectPlay = true,
          supportsDirectStream = true,
          supportsTranscoding = true,
        ),
      )

      val result = dataSource.getPlaybackSession(itemId = "item-1")

      result.isSuccess() shouldBe true
      val session = (result as JellyfinResult.Success).value
      session.playMethod shouldBe PlayMethod.DirectPlay
      session.itemId shouldBe "item-1"
      session.playSessionId shouldBe "session-123"
    }
  }

  @Test
  fun get_playback_session_falls_back_to_direct_stream() {
    runTest {
      fakeService.playbackInfoResult = JellyfinResult.Success(
        createPlaybackInfoResponse(
          supportsDirectPlay = false,
          supportsDirectStream = true,
          supportsTranscoding = true,
        ),
      )

      val result = dataSource.getPlaybackSession(itemId = "item-1")

      result.isSuccess() shouldBe true
      val session = (result as JellyfinResult.Success).value
      session.playMethod shouldBe PlayMethod.DirectStream
    }
  }

  @Test
  fun get_playback_session_falls_back_to_transcode() {
    runTest {
      fakeService.playbackInfoResult = JellyfinResult.Success(
        createPlaybackInfoResponse(
          supportsDirectPlay = false,
          supportsDirectStream = false,
          supportsTranscoding = true,
          transcodingUrl = "/transcode/abc123",
        ),
      )

      val result = dataSource.getPlaybackSession(itemId = "item-1")

      result.isSuccess() shouldBe true
      val session = (result as JellyfinResult.Success).value
      session.playMethod shouldBe PlayMethod.Transcode
      session.streamUrl shouldBe "/transcode/abc123"
    }
  }

  @Test
  fun get_playback_session_returns_error_when_no_sources() {
    runTest {
      fakeService.playbackInfoResult = JellyfinResult.Success(
        PlaybackInfoResponse(
          mediaSources = emptyList(),
          playSessionId = "session-123",
        ),
      )

      val result = dataSource.getPlaybackSession(itemId = "item-1")

      result.shouldBeInstanceOf<JellyfinResult.Error>()
    }
  }

  @Test
  fun get_playback_session_propagates_service_error() {
    runTest {
      fakeService.playbackInfoResult = JellyfinResult.Error(
        message = "Network error",
        isEphemeral = true,
      )

      val result = dataSource.getPlaybackSession(itemId = "item-1")

      result.shouldBeInstanceOf<JellyfinResult.Error>()
    }
  }

  @Test
  fun get_playback_session_maps_media_streams_by_type() {
    runTest {
      fakeService.playbackInfoResult = JellyfinResult.Success(
        createPlaybackInfoResponse(
          supportsDirectPlay = true,
          includeStreams = true,
        ),
      )

      val result = dataSource.getPlaybackSession(itemId = "item-1")

      result.isSuccess() shouldBe true
      val session = (result as JellyfinResult.Success).value
      session.mediaSource.videoStreams.size shouldBe 1
      session.mediaSource.audioStreams.size shouldBe 2
      session.mediaSource.subtitleStreams.size shouldBe 1
    }
  }

  @Test
  fun report_start_sends_correct_info() {
    runTest {
      fakeService.playbackInfoResult = JellyfinResult.Success(
        createPlaybackInfoResponse(supportsDirectPlay = true),
      )
      val session = (dataSource.getPlaybackSession(itemId = "item-1") as JellyfinResult.Success).value

      dataSource.reportStart(session = session)

      fakeService.lastStartInfo?.itemId shouldBe "item-1"
      fakeService.lastStartInfo?.playSessionId shouldBe "session-123"
      fakeService.lastStartInfo?.playMethod shouldBe "DirectPlay"
    }
  }

  @Test
  fun report_progress_sends_position_and_pause_state() {
    runTest {
      fakeService.playbackInfoResult = JellyfinResult.Success(
        createPlaybackInfoResponse(supportsDirectPlay = true),
      )
      val session = (dataSource.getPlaybackSession(itemId = "item-1") as JellyfinResult.Success).value

      dataSource.reportProgress(
        session = session,
        positionTicks = 50_000_000L,
        isPaused = true,
      )

      fakeService.lastProgressInfo?.positionTicks shouldBe 50_000_000L
      fakeService.lastProgressInfo?.isPaused shouldBe true
    }
  }

  @Test
  fun report_stopped_sends_final_position() {
    runTest {
      fakeService.playbackInfoResult = JellyfinResult.Success(
        createPlaybackInfoResponse(supportsDirectPlay = true),
      )
      val session = (dataSource.getPlaybackSession(itemId = "item-1") as JellyfinResult.Success).value

      dataSource.reportStopped(
        session = session,
        positionTicks = 100_000_000L,
      )

      fakeService.lastStopInfo?.positionTicks shouldBe 100_000_000L
    }
  }

  @Test
  fun mark_played_delegates_to_service() {
    runTest {
      dataSource.markPlayed(itemId = "item-1")

      fakeService.lastMarkedPlayedItemId shouldBe "item-1"
    }
  }

  @Test
  fun mark_unplayed_delegates_to_service() {
    runTest {
      dataSource.markUnplayed(itemId = "item-1")

      fakeService.lastMarkedUnplayedItemId shouldBe "item-1"
    }
  }
}

@Suppress("LongMethod")
private fun createPlaybackInfoResponse(
  supportsDirectPlay: Boolean = false,
  supportsDirectStream: Boolean = false,
  supportsTranscoding: Boolean = false,
  transcodingUrl: String? = null,
  includeStreams: Boolean = false,
) = PlaybackInfoResponse(
  mediaSources = listOf(
    MediaSourceInfo(
      id = "source-1",
      name = "Test Source",
      container = "mkv",
      bitrate = 5_000_000,
      supportsDirectPlay = supportsDirectPlay,
      supportsDirectStream = supportsDirectStream,
      supportsTranscoding = supportsTranscoding,
      transcodingUrl = transcodingUrl,
      runTimeTicks = 36_000_000_000L,
      mediaStreams = if(includeStreams) {
        listOf(
          MediaStream(
            type = "Video",
            codec = "h264",
            index = 0,
            width = 1920,
            height = 1080,
            isDefault = true,
          ),
          MediaStream(
            type = "Audio",
            codec = "aac",
            index = 1,
            language = "eng",
            displayTitle = "English",
            isDefault = true,
            channels = 6,
          ),
          MediaStream(
            type = "Audio",
            codec = "ac3",
            index = 2,
            language = "spa",
            displayTitle = "Spanish",
          ),
          MediaStream(
            type = "Subtitle",
            codec = "srt",
            index = 3,
            language = "eng",
            displayTitle = "English",
            isExternal = true,
          ),
        )
      }
      else {
        emptyList()
      },
    ),
  ),
  playSessionId = "session-123",
)

@Suppress("TooManyFunctions")
private class FakeJellyfinPlaybackService : JellyfinPlaybackService {
  var playbackInfoResult: JellyfinResult<PlaybackInfoResponse> = JellyfinResult.Error(
    message = "Not configured",
    isEphemeral = false,
  )
  var lastStartInfo: PlaybackStartInfo? = null
  var lastProgressInfo: PlaybackProgressInfo? = null
  var lastStopInfo: PlaybackStopInfo? = null
  var lastMarkedPlayedItemId: String? = null
  var lastMarkedUnplayedItemId: String? = null

  override suspend fun getPlaybackInfo(
    itemId: String,
  ): JellyfinResult<PlaybackInfoResponse> = playbackInfoResult

  override fun getVideoStreamUrl(
    itemId: String,
    mediaSourceId: String?,
    container: String?,
    audioCodec: String?,
    videoCodec: String?,
    maxWidth: Int?,
    maxHeight: Int?,
  ): String = "https://server/video/$itemId/stream${container?.let { ".$it" }.orEmpty()}"

  override fun getAudioStreamUrl(
    itemId: String,
    mediaSourceId: String?,
    container: String?,
    maxBitrate: Int?,
  ): String = "https://server/audio/$itemId/stream"

  override suspend fun reportPlaybackStart(
    info: PlaybackStartInfo,
  ): JellyfinResult<Unit> {
    lastStartInfo = info
    return JellyfinResult.Success(Unit)
  }

  override suspend fun reportPlaybackProgress(
    info: PlaybackProgressInfo,
  ): JellyfinResult<Unit> {
    lastProgressInfo = info
    return JellyfinResult.Success(Unit)
  }

  override suspend fun reportPlaybackStopped(
    info: PlaybackStopInfo,
  ): JellyfinResult<Unit> {
    lastStopInfo = info
    return JellyfinResult.Success(Unit)
  }

  override suspend fun markPlayed(itemId: String): JellyfinResult<Unit> {
    lastMarkedPlayedItemId = itemId
    return JellyfinResult.Success(Unit)
  }

  override suspend fun markUnplayed(itemId: String): JellyfinResult<Unit> {
    lastMarkedUnplayedItemId = itemId
    return JellyfinResult.Success(Unit)
  }
}
