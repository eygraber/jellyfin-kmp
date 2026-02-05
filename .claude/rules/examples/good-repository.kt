// Exemplar Repository following all project conventions
// See .claude/rules/architecture.md and data-persistence.md for complete rules

package com.com.superdo.data.example

import com.com.superdo.common.SuperDoResult
import com.com.superdo.common.andThen
import com.com.superdo.common.fold
import com.com.superdo.entity.Example
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ✅ Public interface in public module
interface ExampleRepository {
  fun observeExamples(): Flow<List<Example>>
  suspend fun loadExamples(): SuperDoResult<List<Example>>
  suspend fun saveExample(example: Example): SuperDoResult<Unit>
}

// ✅ Implementation in impl module
// ✅ Use @ContributesBinding for automatic DI binding
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class) // ✅ Scoped singleton
class RealExampleRepository(
  private val remoteDataSource: ExampleRemoteDataSource, // ✅ Inject dependencies
  private val localDataSource: ExampleLocalDataSource,
) : ExampleRepository {

  // ✅ Use Flow for reactive data
  override fun observeExamples(): Flow<List<Example>> {
    return localDataSource.observeExamples()
  }

  // ✅ Use SuperDoResult for operation outcomes
  override suspend fun loadExamples(): SuperDoResult<List<Example>> {
    // ✅ Load from remote, save to local using andThen for side effects
    return remoteDataSource.fetchExamples()
      .andThen { examples ->
        localDataSource.saveExamples(examples)
      }
      .fold(
        onSuccess = { examples -> SuperDoResult.Success(examples) },
        onFailure = { error ->
          // ✅ Fall back to local cache on error
          val cached = localDataSource.getExamples()
          if (cached.isNotEmpty()) {
            SuperDoResult.Success(cached)
          } else {
            SuperDoResult.Failure(error)
          }
        },
      )
  }

  override suspend fun saveExample(example: Example): SuperDoResult<Unit> {
    // ✅ Save to remote first, then local using andThen for side effects
    return remoteDataSource.saveExample(example)
      .andThen {
        localDataSource.saveExample(example)
      }
  }
}

// ✅ Separate data source interfaces
interface ExampleRemoteDataSource {
  suspend fun fetchExamples(): SuperDoResult<List<Example>>
  suspend fun saveExample(example: Example): SuperDoResult<Unit>
}

interface ExampleLocalDataSource {
  fun observeExamples(): Flow<List<Example>>
  suspend fun getExamples(): List<Example>
  suspend fun saveExamples(examples: List<Example>)
  suspend fun saveExample(example: Example)
}

// ✅ Fake implementation for testing
class FakeExampleRepository : ExampleRepository {
  private val examples = mutableListOf<Example>()
  private val _flow = kotlinx.coroutines.flow.MutableStateFlow<List<Example>>(emptyList())

  override fun observeExamples(): Flow<List<Example>> = _flow

  override suspend fun loadExamples(): SuperDoResult<List<Example>> {
    return SuperDoResult.Success(examples.toList())
  }

  override suspend fun saveExample(example: Example): SuperDoResult<Unit> {
    examples.add(example)
    _flow.value = examples.toList()
    return SuperDoResult.Success(Unit)
  }

  // Test helper
  fun setExamples(data: List<Example>) {
    examples.clear()
    examples.addAll(data)
    _flow.value = examples.toList()
  }
}
