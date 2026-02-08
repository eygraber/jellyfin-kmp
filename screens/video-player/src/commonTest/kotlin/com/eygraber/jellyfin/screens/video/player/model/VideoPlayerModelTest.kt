package com.eygraber.jellyfin.screens.video.player.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.playback.PlayMethod
import com.eygraber.jellyfin.data.playback.PlaybackMediaSource
import com.eygraber.jellyfin.data.playback.PlaybackRepository
import com.eygraber.jellyfin.data.playback.PlaybackSession
import com.eygraber.jellyfin.services.player.PlaybackState
import com.eygraber.jellyfin.services.player.VideoPlayerService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class VideoPlayerModelTest {
  private val fakeRepository = FakePlaybackRepository()
  private val fakePlayerService = FakeVideoPlayerService()
  private val model = VideoPlayerModel(
    playbackRepository = fakeRepository,
    playerService = fakePlayerService,
  )

  @Test
  fun initial_state_is_loading() {
    model.stateForTest.isLoading shouldBe true
    model.stateForTest.session shouldBe null
    model.stateForTest.error shouldBe null
  }

  @Test
  fun load_and_play_success_initializes_player() {
    runTest {
      fakeRepository.getPlaybackSessionResult = JellyfinResult.Success(createSession())

      model.loadAndPlay(itemId = "item-1", startPositionMs = 5_000L)

      model.stateForTest.isLoading shouldBe false
      model.stateForTest.session shouldNotBe null
      model.stateForTest.session?.itemId shouldBe "item-1"
      fakePlayerService.lastInitUrl shouldBe "https://server/stream"
      fakePlayerService.lastInitPosition shouldBe 5_000L
    }
  }

  @Test
  fun load_and_play_success_reports_start() {
    runTest {
      fakeRepository.getPlaybackSessionResult = JellyfinResult.Success(createSession())

      model.loadAndPlay(itemId = "item-1", startPositionMs = 0L)

      fakeRepository.lastReportStartSession shouldNotBe null
    }
  }

  @Test
  fun load_and_play_failure_sets_error() {
    runTest {
      fakeRepository.getPlaybackSessionResult = JellyfinResult.Error(
        message = "Network error",
        isEphemeral = true,
      )

      model.loadAndPlay(itemId = "item-1", startPositionMs = 0L)

      model.stateForTest.isLoading shouldBe false
      model.stateForTest.error shouldBe VideoPlayerModelError.SessionFailed
    }
  }

  @Test
  fun update_from_player_state_propagates_state() {
    val newState = PlaybackState(
      isPlaying = true,
      currentPositionMs = 10_000L,
      durationMs = 100_000L,
    )

    model.updateFromPlayerState(newState)

    model.stateForTest.playbackState.isPlaying shouldBe true
    model.stateForTest.playbackState.currentPositionMs shouldBe 10_000L
  }

  @Test
  fun update_from_player_state_with_error_sets_playback_failed() {
    val errorState = PlaybackState(
      hasError = true,
      errorMessage = "Codec error",
    )

    model.updateFromPlayerState(errorState)

    model.stateForTest.error shouldBe VideoPlayerModelError.PlaybackFailed
  }

  @Test
  fun play_delegates_to_player_service() {
    model.play()

    fakePlayerService.playCount shouldBe 1
  }

  @Test
  fun pause_delegates_to_player_service() {
    model.pause()

    fakePlayerService.pauseCount shouldBe 1
  }

  @Test
  fun seek_delegates_to_player_service() {
    model.seekTo(50_000L)

    fakePlayerService.lastSeekPosition shouldBe 50_000L
  }

  @Test
  fun toggle_controls_toggles_visibility() {
    model.stateForTest.isControlsVisible shouldBe true

    model.toggleControls()
    model.stateForTest.isControlsVisible shouldBe false

    model.toggleControls()
    model.stateForTest.isControlsVisible shouldBe true
  }

  @Test
  fun stop_reports_stopped_and_releases_player() {
    runTest {
      fakeRepository.getPlaybackSessionResult = JellyfinResult.Success(createSession())
      model.loadAndPlay(itemId = "item-1", startPositionMs = 0L)

      model.updateFromPlayerState(
        PlaybackState(currentPositionMs = 30_000L),
      )

      model.stop()

      fakeRepository.lastReportStoppedPositionTicks shouldBe 300_000_000L
      fakePlayerService.releaseCount shouldBe 1
    }
  }

  @Test
  fun report_progress_sends_current_position() {
    runTest {
      fakeRepository.getPlaybackSessionResult = JellyfinResult.Success(createSession())
      model.loadAndPlay(itemId = "item-1", startPositionMs = 0L)

      model.updateFromPlayerState(
        PlaybackState(
          isPlaying = true,
          currentPositionMs = 20_000L,
        ),
      )

      model.reportProgress()

      fakeRepository.lastReportProgressPositionTicks shouldBe 200_000_000L
      fakeRepository.wasLastReportProgressPaused shouldBe false
    }
  }
}

private fun createSession() = PlaybackSession(
  itemId = "item-1",
  playSessionId = "session-1",
  mediaSource = PlaybackMediaSource(
    id = "source-1",
    name = "Test",
    container = "mkv",
    bitrate = 5_000_000,
    runtimeTicks = 1_000_000_000L,
    canDirectPlay = true,
    canDirectStream = false,
    canTranscode = false,
    transcodingUrl = null,
    videoStreams = emptyList(),
    audioStreams = emptyList(),
    subtitleStreams = emptyList(),
  ),
  streamUrl = "https://server/stream",
  playMethod = PlayMethod.DirectPlay,
)

@Suppress("TooManyFunctions")
private class FakePlaybackRepository : PlaybackRepository {
  var getPlaybackSessionResult: JellyfinResult<PlaybackSession> = JellyfinResult.Error(
    message = "Not configured",
    isEphemeral = false,
  )
  var lastReportStartSession: PlaybackSession? = null
  var lastReportProgressPositionTicks: Long? = null
  var wasLastReportProgressPaused: Boolean? = null
  var lastReportStoppedPositionTicks: Long? = null

  override suspend fun getPlaybackSession(
    itemId: String,
  ): JellyfinResult<PlaybackSession> = getPlaybackSessionResult

  override suspend fun reportPlaybackStart(
    session: PlaybackSession,
  ): JellyfinResult<Unit> {
    lastReportStartSession = session
    return JellyfinResult.Success(Unit)
  }

  override suspend fun reportPlaybackProgress(
    session: PlaybackSession,
    positionTicks: Long,
    isPaused: Boolean,
  ): JellyfinResult<Unit> {
    lastReportProgressPositionTicks = positionTicks
    wasLastReportProgressPaused = isPaused
    return JellyfinResult.Success(Unit)
  }

  override suspend fun reportPlaybackStopped(
    session: PlaybackSession,
    positionTicks: Long?,
  ): JellyfinResult<Unit> {
    lastReportStoppedPositionTicks = positionTicks
    return JellyfinResult.Success(Unit)
  }

  override suspend fun markPlayed(itemId: String): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)

  override suspend fun markUnplayed(itemId: String): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)
}

private class FakeVideoPlayerService : VideoPlayerService {
  private val _playbackState = MutableStateFlow(PlaybackState.Idle)
  override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  var lastInitUrl: String? = null
  var lastInitPosition: Long? = null
  var playCount: Int = 0
  var pauseCount: Int = 0
  var lastSeekPosition: Long? = null
  var releaseCount: Int = 0

  override fun initialize(streamUrl: String, startPositionMs: Long) {
    lastInitUrl = streamUrl
    lastInitPosition = startPositionMs
  }

  override fun play() {
    playCount++
  }

  override fun pause() {
    pauseCount++
  }

  override fun seekTo(positionMs: Long) {
    lastSeekPosition = positionMs
  }

  override fun release() {
    releaseCount++
  }
}
