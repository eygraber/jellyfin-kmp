// Example: Well-structured Worker with DI integration
package com.eygraber.jellyfin.data.user

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.auth.UserAuthRepository
import com.eygraber.jellyfin.di.scopes.WorkScope
import com.eygraber.jellyfin.services.work.graphFactory
import com.juul.khronicle.Log
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.SingleIn

/**
 * Background worker that syncs users from the backend.
 *
 * Runs periodically or on-demand to keep local user data fresh.
 */
class SyncUsersWorker(
  appContext: Context,
  params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

  // Lazy graph access - defers DI graph creation until needed
  private val graph by lazy {
    graphFactory<WorkerGraph.Factory>().createSyncUsersWorkerGraph()
  }

  override suspend fun doWork(): Result {
    // Always check auth state if worker requires authentication
    if(!graph.userAuthRepository.isLoggedIn()) {
      return fail("the user is not logged in")
    }

    // Map JellyfinResult to WorkManager Result types
    return when(val result = graph.userRepository.fetchUsers()) {
      is JellyfinResult.Error -> when {
        // Ephemeral errors (network, timeout) should retry
        result.isEphemeral -> Result.retry().also {
          Log.debug { "Retrying syncing users because of an ephemeral error (${result.message.orEmpty()})" }
        }
        // Non-ephemeral errors (4xx, business logic) should fail
        else -> fail("we received a non ephemeral error (${result.message.orEmpty()})")
      }
      is JellyfinResult.Success<*> -> Result.success()
    }
  }

  private fun fail(reason: String) = Result.failure().also {
    Log.warn { "Not syncing users because $reason" }
  }

  /**
   * Worker-specific DI graph.
   *
   * Scoped to this worker class - each worker instance gets its own graph.
   * Can access @SingleIn(AppScope) dependencies through the DI graph.
   */
  @GraphExtension(SyncUsersWorker::class)  // Use worker class as scope
  interface WorkerGraph {
    // Declare dependencies needed by this worker
    val userRepository: UserRepository
    val userAuthRepository: UserAuthRepository

    /**
     * Factory contributed to WorkScope, not AppScope.
     * This allows JellyfinWorkGraph to create worker graphs.
     */
    @ContributesTo(WorkScope::class)
    @GraphExtension.Factory
    interface Factory {
      fun createSyncUsersWorkerGraph(): WorkerGraph
    }
  }
}
