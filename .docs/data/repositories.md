# Repository Pattern

Coordinates between local and remote data sources.

## Interface (public/)

```kotlin
interface MyFeatureRepository {
  val myDataFlow: Flow<List<MyEntity>>

  suspend fun fetchData(
    retryPolicy: RetryPolicy = RetryPolicy.MaxAttempts(5),
  ): JellyfinResult<Unit>

  suspend fun getData(id: String): MyEntity?
}
```

## Implementation (impl/)

```kotlin
@Inject
@ContributesBinding(AppScope::class)
class RealMyFeatureRepository(
  private val localDataSource: MyFeatureLocalDataSource,
  private val remoteDataSource: MyFeatureRemoteDataSource,
) : MyFeatureRepository {

  override val myDataFlow get() = localDataSource.myDataFlow

  override suspend fun fetchData(
    retryPolicy: RetryPolicy,
  ) = retryJellyfinResult(retryPolicy) {
    remoteDataSource
      .fetchData()
      .andThen { data ->
        localDataSource.upsertData(data)
      }
  }.mapToUnit()

  override suspend fun getData(id: String) =
    localDataSource.getData(id)
}
```

## Key Patterns

### Fetch and Cache
```kotlin
remoteDataSource
  .fetchData()
  .andThen { localDataSource.upsertData(it) }
```

### Observe Local Data
```kotlin
override val dataFlow get() = localDataSource.dataFlow
```

### Retry Logic
```kotlin
retryJellyfinResult(RetryPolicy.MaxAttempts(5)) {
  remoteDataSource.fetchData()
}
```

## Fake (fake/)

```kotlin
class FakeMyFeatureRepository(
  var onFetchData: suspend () -> Unit = {},
  override val myDataFlow: Flow<List<MyEntity>> = flowOf(emptyList()),
  var fetchDataResult: JellyfinResult<Unit> = JellyfinResult.Success(),
) : MyFeatureRepository {

  private val _data = mutableMapOf<String, MyEntity>()

  override suspend fun fetchData(retryPolicy: RetryPolicy) =
    fetchDataResult.also { onFetchData() }

  override suspend fun getData(id: String) = _data[id]

  fun setData(entities: List<MyEntity>) {
    _data.clear()
    entities.forEach { _data[it.id] = it }
  }
}
```
