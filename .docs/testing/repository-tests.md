# Repository Tests

Test data layer coordination.

## Basic Pattern

```kotlin
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MyRepositoryTest {
  @Test
  fun `when local data exists, returns cache`() = runTest {
    val fakeLocal = FakeLocalDataSource(cachedData = sampleData)
    val fakeRemote = FakeRemoteDataSource()
    val repo = MyRepository(fakeLocal, fakeRemote)

    val result = repo.getData()

    result shouldBe JellyfinResult.Success(sampleData)
    fakeRemote.callCount shouldBe 0
  }

  @Test
  fun `when local empty, fetches remote and caches`() = runTest {
    val fakeLocal = FakeLocalDataSource(cachedData = null)
    val fakeRemote = FakeRemoteDataSource(remoteData = sampleData)
    val repo = MyRepository(fakeLocal, fakeRemote)

    val result = repo.getData()

    result shouldBe JellyfinResult.Success(sampleData)
    fakeLocal.savedData shouldBe sampleData
  }
}
```

## Test Scenarios

### Cache Behavior
```kotlin
@Test
fun `stale cache triggers refresh`() = runTest {
  val fakeLocal = FakeLocalDataSource(
    cachedData = staleData,
    isStale = true,
  )
  // ...
}
```

### Error Handling
```kotlin
@Test
fun `when remote fails, returns cached data`() = runTest {
  val fakeLocal = FakeLocalDataSource(cachedData = oldData)
  val fakeRemote = FakeRemoteDataSource().apply { shouldFail = true }
  val repo = MyRepository(fakeLocal, fakeRemote)

  val result = repo.getData()

  result shouldBe JellyfinResult.Success(oldData)
}

@Test
fun `when both fail, returns error`() = runTest {
  val fakeLocal = FakeLocalDataSource(cachedData = null)
  val fakeRemote = FakeRemoteDataSource().apply { shouldFail = true }
  val repo = MyRepository(fakeLocal, fakeRemote)

  val result = repo.getData()

  result.isFailure shouldBe true
}
```

### Sync Operations
```kotlin
@Test
fun `sync updates local from remote`() = runTest {
  val fakeLocal = FakeLocalDataSource()
  val fakeRemote = FakeRemoteDataSource(remoteData = newData)
  val repo = MyRepository(fakeLocal, fakeRemote)

  repo.sync()

  fakeLocal.savedData shouldBe newData
}
```

## Creating Fake Data Sources

```kotlin
class FakeLocalDataSource : LocalDataSource {
  var cachedData: Data? = null
  var savedData: Data? = null

  override suspend fun getData() = cachedData
  override suspend fun saveData(data: Data) {
    savedData = data
  }
}

class FakeRemoteDataSource : RemoteDataSource {
  var remoteData: Data? = null
  var shouldFail = false
  var callCount = 0

  override suspend fun fetchData(): JellyfinResult<Data> {
    callCount++
    return when {
      shouldFail -> JellyfinResult.Failure(Exception("Remote error"))
      remoteData != null -> JellyfinResult.Success(remoteData!!)
      else -> JellyfinResult.Failure(Exception("No data"))
    }
  }
}
```
