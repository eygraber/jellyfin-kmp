# Data Layer

Data layer components: repositories, data sources, entities.

## Contents

- [repositories.md](repositories.md) - Repository pattern
- [datastore.md](datastore.md) - DataStore usage

## Module Structure

```
data/<feature>/
+-- public/     # Interfaces, entities
+-- impl/       # Real implementations
+-- fake/       # Test doubles
```

## Key Responsibilities

| Component        | Purpose                           |
|------------------|-----------------------------------|
| Repository       | Coordinates local/remote, caching |
| LocalDataSource  | Database operations               |
| RemoteDataSource | API calls                         |

## Quick Example

```kotlin
// public/
interface UserRepository {
  val usersFlow: Flow<List<User>>
  suspend fun fetchUsers(): TemplateResult<Unit>
}

// impl/
@Inject
@ContributesBinding(AppScope::class)
class RealUserRepository(
  private val localDataSource: UserLocalDataSource,
  private val remoteDataSource: UserRemoteDataSource,
) : UserRepository {
  override val usersFlow get() = localDataSource.usersFlow

  override suspend fun fetchUsers() =
    remoteDataSource
      .fetchUsers()
      .andThen { localDataSource.upsertUsers(it) }
}
```

## Error Handling

Use result types for operations that can fail:

```kotlin
suspend fun getData(): TemplateResult<Data>

// Extensions
result.doOnSuccess { /* side effect */ }
result.andThen { /* result-aware side effect */ }
result.flatMap {}
result.mapSuccessTo { /* map success value */ }
result.flatMapSuccessTo { /* map success result */ }
result.mapToUnit()
```

### doOnSuccess vs andThen

**Prefer `andThen` in most cases:**
- Wraps callbacks in `runResult`, propagating failures
- Use for side effects that can fail (database writes, network calls, file I/O)
- Ensures failures in the callback don't go unnoticed

**Use `doOnSuccess` sparingly:**
- Ignores exceptions thrown in the callback
- Only for non-critical side effects (logging, metrics)
- When you explicitly don't care about callback failures
