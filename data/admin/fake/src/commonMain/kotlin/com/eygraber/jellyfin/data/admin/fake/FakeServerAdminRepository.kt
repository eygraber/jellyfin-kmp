package com.eygraber.jellyfin.data.admin.fake

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.admin.AdminServerConfiguration
import com.eygraber.jellyfin.data.admin.AdminServerInfo
import com.eygraber.jellyfin.data.admin.ScheduledTask
import com.eygraber.jellyfin.data.admin.ServerAdminRepository

/**
 * In-memory fake of [ServerAdminRepository] for tests.
 */
class FakeServerAdminRepository(
  initialServerInfo: AdminServerInfo = AdminServerInfo(
    id = "fake-server",
    serverName = "Fake Server",
    version = "0.0.0",
    operatingSystem = null,
    productName = null,
    hasPendingRestart = false,
    canSelfRestart = true,
    isShuttingDown = false,
    cachePath = null,
    logPath = null,
  ),
  initialConfiguration: AdminServerConfiguration = AdminServerConfiguration(
    serverName = "Fake Server",
    preferredMetadataLanguage = "en",
    metadataCountryCode = "US",
    cachePath = null,
    metadataPath = null,
    minResumePct = 5,
    maxResumePct = 90,
    minResumeDurationSeconds = 300,
    libraryMonitorDelay = 60,
    quickConnectAvailable = true,
    enableMetrics = false,
  ),
  initialTasks: List<ScheduledTask> = emptyList(),
) : ServerAdminRepository {
  var serverInfo: AdminServerInfo = initialServerInfo
  var configuration: AdminServerConfiguration = initialConfiguration
  private val mutableTasks = initialTasks.associateBy { it.id }.toMutableMap()

  val tasks: List<ScheduledTask> get() = mutableTasks.values.toList()

  var nextResult: JellyfinResult<Any?>? = null
  var restartCount: Int = 0
    private set
  var shutdownCount: Int = 0
    private set

  val startedTaskIds: MutableList<String> = mutableListOf()
  val stoppedTaskIds: MutableList<String> = mutableListOf()

  override suspend fun getServerInfo(): JellyfinResult<AdminServerInfo> =
    consumeOverride() ?: JellyfinResult.Success(serverInfo)

  override suspend fun getConfiguration(): JellyfinResult<AdminServerConfiguration> =
    consumeOverride() ?: JellyfinResult.Success(configuration)

  override suspend fun updateConfiguration(
    configuration: AdminServerConfiguration,
  ): JellyfinResult<Unit> {
    consumeOverride<Unit>()?.let { return it }
    this.configuration = configuration
    return JellyfinResult.Success(Unit)
  }

  override suspend fun restartServer(): JellyfinResult<Unit> {
    consumeOverride<Unit>()?.let { return it }
    restartCount += 1
    return JellyfinResult.Success(Unit)
  }

  override suspend fun shutdownServer(): JellyfinResult<Unit> {
    consumeOverride<Unit>()?.let { return it }
    shutdownCount += 1
    return JellyfinResult.Success(Unit)
  }

  override suspend fun getScheduledTasks(
    isHidden: Boolean?,
    isEnabled: Boolean?,
  ): JellyfinResult<List<ScheduledTask>> = consumeOverride()
    ?: JellyfinResult.Success(
      tasks.filter { task ->
        isHidden == null || task.isHidden == isHidden
      },
    )

  override suspend fun getScheduledTask(taskId: String): JellyfinResult<ScheduledTask> =
    consumeOverride()
      ?: mutableTasks[taskId]?.let { JellyfinResult.Success(it) }
      ?: JellyfinResult.Error(message = "Not found", isEphemeral = false)

  override suspend fun startScheduledTask(taskId: String): JellyfinResult<Unit> {
    consumeOverride<Unit>()?.let { return it }
    startedTaskIds += taskId
    return JellyfinResult.Success(Unit)
  }

  override suspend fun stopScheduledTask(taskId: String): JellyfinResult<Unit> {
    consumeOverride<Unit>()?.let { return it }
    stoppedTaskIds += taskId
    return JellyfinResult.Success(Unit)
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T> consumeOverride(): JellyfinResult<T>? {
    val override = nextResult ?: return null
    nextResult = null
    return override as JellyfinResult<T>
  }
}
