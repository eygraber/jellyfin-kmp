// Exemplar Repository following all project conventions
// See .claude/rules/architecture.md and data-persistence.md for complete rules

package com.template.data.example

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// Public interface in public module
interface ExampleRepository {
  fun observeExamples(): Flow<List<Example>>
  suspend fun loadExamples(): TemplateResult<List<Example>>
  suspend fun saveExample(example: Example): TemplateResult<Unit>
}

// Implementation in impl module
// Use @ContributesBinding for automatic DI binding
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class) // Scoped singleton
class RealExampleRepository(
  private val remoteDataSource: ExampleRemoteDataSource, // Inject dependencies
  private val localDataSource: ExampleLocalDataSource,
) : ExampleRepository {

  // Use Flow for reactive data
  override fun observeExamples(): Flow<List<Example>> {
    return localDataSource.observeExamples()
  }

  // Use result type for operation outcomes
  override suspend fun loadExamples(): TemplateResult<List<Example>> {
    // Load from remote, save to local using andThen for side effects
    return remoteDataSource.fetchExamples()
      .andThen { examples ->
        localDataSource.saveExamples(examples)
      }
      .fold(
        onSuccess = { examples -> TemplateResult.Success(examples) },
        onFailure = { error ->
          // Fall back to local cache on error
          val cached = localDataSource.getExamples()
          if (cached.isNotEmpty()) {
            TemplateResult.Success(cached)
          } else {
            TemplateResult.Failure(error)
          }
        },
      )
  }

  override suspend fun saveExample(example: Example): TemplateResult<Unit> {
    // Save to remote first, then local using andThen for side effects
    return remoteDataSource.saveExample(example)
      .andThen {
        localDataSource.saveExample(example)
      }
  }
}

// Separate data source interfaces
interface ExampleRemoteDataSource {
  suspend fun fetchExamples(): TemplateResult<List<Example>>
  suspend fun saveExample(example: Example): TemplateResult<Unit>
}

interface ExampleLocalDataSource {
  fun observeExamples(): Flow<List<Example>>
  suspend fun getExamples(): List<Example>
  suspend fun saveExamples(examples: List<Example>)
  suspend fun saveExample(example: Example)
}

// Fake implementation for testing
class FakeExampleRepository : ExampleRepository {
  private val examples = mutableListOf<Example>()
  private val _flow = MutableStateFlow<List<Example>>(emptyList())

  override fun observeExamples(): Flow<List<Example>> = _flow

  override suspend fun loadExamples(): TemplateResult<List<Example>> {
    return TemplateResult.Success(examples.toList())
  }

  override suspend fun saveExample(example: Example): TemplateResult<Unit> {
    examples.add(example)
    _flow.value = examples.toList()
    return TemplateResult.Success(Unit)
  }

  // Test helper
  fun setExamples(data: List<Example>) {
    examples.clear()
    examples.addAll(data)
    _flow.value = examples.toList()
  }
}

// Entity type
data class Example(val id: String, val name: String)

// Result type placeholder
sealed interface TemplateResult<out T> {
  data class Success<T>(val value: T) : TemplateResult<T>
  data class Failure(val error: Throwable) : TemplateResult<Nothing>
}
