---
paths:
  - "data/**/*.kt"
---

# Module Structure
data/<feature>/
├── public/     # Repository interface, entities
├── impl/       # RealRepository, LocalDataSource, RemoteDataSource
└── fake/       # FakeRepository for tests

# Repository Pattern
Coordinate between local (SQLDelight) and remote (Ktorfit) sources
override val dataFlow get() = localDataSource.dataFlow
override suspend fun fetchData() = remoteDataSource.fetchData().doOnSuccess { localDataSource.upsertData(it) }

## Repository Architecture Principles

### Repositories Must Be Stateless
- Repositories should NOT hold state (no `@SingleIn` singleton scope)
- Repositories should NOT expose StateFlow of app state
- State should be maintained at the screen/compositor level
- Each screen loads and tracks its own state from the repository

### LocalDataSource Pattern
- Repositories MUST use LocalDataSource for database operations (never access DB directly)
- LocalDataSource handles all SQLDelight database interactions
- LocalDataSource should NOT be a singleton unless there's an explicit need (connection pooling is handled elsewhere)
- Repository delegates to LocalDataSource for all persistence operations

### RemoteDataSource Pattern
- Repositories MUST use RemoteDataSource for network operations (never access API directly)
- RemoteDataSource handles all Ktorfit API interactions
- RemoteDataSource should NOT be a singleton (no `@SingleIn` scope)
- RemoteDataSource should NOT hold state (no caching, no StateFlow)
- Repository delegates to RemoteDataSource for all network operations

```kotlin
// ❌ Bad: Repository directly uses API, RemoteDataSource holds state
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)  // Bad: stateful singleton
class RealAuthRepository(
  private val api: AuthApi,  // Bad: direct API access
) : AuthRepository {
  private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)  // Bad: holding state
  override val authState: StateFlow<AuthState> = _authState  // Bad: exposing state
}

@SingleIn(AppScope::class)  // Bad: singleton RemoteDataSource
class AuthRemoteDataSource(private val api: AuthApi) {
  private var cachedUser: User? = null  // Bad: caching in RemoteDataSource
}

// ✅ Good: Repository uses DataSources, all are stateless
@ContributesBinding(AppScope::class)
class RealAuthRepository(
  private val localDataSource: AuthLocalDataSource,
  private val remoteDataSource: AuthRemoteDataSource,
) : AuthRepository {
  override suspend fun getCurrentUser(): User? = localDataSource.getCurrentUser()
  override suspend fun fetchCurrentUser() =
    remoteDataSource
      .fetchCurrentUser()
      .andThen { localDataSource.upsertUser(it) }
}

class AuthRemoteDataSource(private val api: AuthApi) {
  suspend fun fetchCurrentUser(): TemplateResult<User> = api.getCurrentUser().toResult()
}
```

# SQLDelight
Write SQL in .sq files to generate type-safe Kotlin APIs
Write .sqm migration files when schema changes are made
Use custom type adapters for IDs
Use upsert pattern with ON CONFLICT
Case is a case-insensitive reserved keyword in sqlite, so we need to escape it as `Case`
Ensure FK references are covered by an index
Ensure there is a relevant ON DELETE clause present for FK references when it is warranted
After making changes to .sq files, running ./gradlew :services:sqldelight:assembleDebug might be required

## SQLite Migrations
Prefer ALTER TABLE statements (https://www.sqlite.org/lang_altertable.html) when possible:
- ALTER TABLE RENAME TO - rename table
- ALTER TABLE RENAME COLUMN - rename column
- ALTER TABLE ADD COLUMN - add column
- ALTER TABLE DROP COLUMN - drop column

Only use copy-drop-rename pattern when ALTER TABLE cannot accomplish the change:
1. CREATE TABLE new_table with desired schema
2. INSERT INTO new_table SELECT ... FROM old_table
3. DROP TABLE old_table
4. ALTER TABLE new_table RENAME TO old_table
5. Recreate indexes, triggers, views

Where dispatchers is an instance of TemplateDbDispatchers
withDbReadContext(dispatchers) { query.executeAsOneOrNull() }  # Read
db.withTransaction(dispatchers) { queries.upsert(...) }        # Write
queries.selectAll().asFlow().mapToList(dispatchers)            # Observe

# Ktorfit
API interfaces are internal to impl module
Use TemplateResponse<T> return type, convert with .toResult()
TemplateResponse will SHOULD be parameterized with either kotlinx.serialization JsonArray or JsonObject

# Error Handling
Use TemplateResult<T> for fallible operations. See [error-handling](error-handling.md) for more information.

# DataStore
Use DataStore for simple key-value persistence needs

# Complete Example
See .claude/rules/examples/good-repository.kt for a complete repository implementation

### Documentation Reference
For complete patterns: .docs/data/
