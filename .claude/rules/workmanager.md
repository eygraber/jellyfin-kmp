---
paths:
  - "**/worker/**/*.kt"
---

# WorkManager DI Rules

When creating or modifying WorkManager workers:

## Worker Structure

```kotlin
class MyWorker(
  appContext: Context,
  params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

  private val graph by lazy {
    graphFactory<WorkerGraph.Factory>().createMyWorkerGraph()
  }

  override suspend fun doWork(): Result {
    // Map SuperDoResult to Result.success/retry/failure
  }

  @GraphExtension(MyWorker::class)
  interface WorkerGraph {
    val dependency: SomeDependency

    @ContributesTo(WorkScope::class)
    @GraphExtension.Factory
    interface Factory {
      fun createMyWorkerGraph(): WorkerGraph
    }
  }
}
```

## Key Patterns

1. **Scope**: Use worker class itself as scope marker:
   - `@GraphExtension(MyWorker::class)`

2. **Factory**: Contributes to `WorkScope`, not `AppScope`:
   - `@ContributesTo(WorkScope::class)`
   - `@GraphExtension.Factory`

3. **Graph access**: Always use `by lazy` delegation

4. **Result mapping**:
   ```kotlin
   when(result) {
     is SuperDoResult.Error ->
       if (result.isEphemeral) Result.retry() else Result.failure()
     is SuperDoResult.Success -> Result.success()
   }
   ```

5. **Auth checks**: Check `isLoggedIn()` before work if needed

6. **Logging**: Use `Log.info/debug/warn` from khronicle

## Testing

```kotlin
private fun workerContext(...) = createWorkerContext(
  object : SuperDoWorkGraph, MyWorker.WorkerGraph.Factory {
    override fun createMyWorkerGraph() = object : MyWorker.WorkerGraph {
      override val dependency = fakeDependency
    }
  }
)

val worker = TestListenableWorkerBuilder<MyWorker>(context).build()
worker.doWork() shouldBe Result.success()
```

## Scheduling

Use extensions from `com.com.superdo.services.work`:
- `requiresNetworkConnection()`
- `isExpeditedFallingBackToNonExpedited()`
- `tagAsAuthenticated()`

## Import Requirements

```kotlin
import com.com.superdo.di.scopes.WorkScope
import com.com.superdo.services.work.graphFactory
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.SingleIn
```

### Documentation
For complete patterns: .docs/di/workmanager.md
