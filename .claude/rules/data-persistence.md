---
paths:
  - "data/**/*.kt"
---

# Module Structure
data/<feature>/
+-- public/     # Repository interface, entities
+-- impl/       # RealRepository, DataSources
+-- fake/       # FakeRepository for tests

# Repository Pattern
Coordinate between local and remote sources
override val dataFlow get() = localDataSource.dataFlow
override suspend fun fetchData() = remoteDataSource.fetchData().doOnSuccess { localDataSource.upsertData(it) }

# Error Handling
Use result types for fallible operations
Use retry policies for retries
.doOnSuccess {} for side effects
.mapToUnit() for Unit results

# DataStore
Use DataStore for simple key-value persistence needs

# Complete Example
See .claude/rules/examples/good-repository.kt for a complete repository implementation

### Documentation Reference
For complete patterns: .docs/data/
