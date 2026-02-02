# DI in Tests

## Unit Tests

Usually don't need full DI. Prefer:

1. **Manual DI** - Create subject directly with fakes
2. **Test-specific graph** - When DI is needed

### Manual DI (Preferred)

```kotlin
class MyModelTest {
  @Test
  fun `test something`() {
    val fakeRepo = FakeUserRepository()
    val model = MyModel(fakeRepo) // Manual injection

    // test...
  }
}
```

### Test Graph

When you need DI in tests:

```kotlin
@DependencyGraph
abstract class TestGraph {
  abstract val myModel: MyModel

  @Provides fun fakeRepo(): UserRepository = FakeUserRepository()
}
```

**Gradle**:
```kotlin
plugins {
  alias(libs.plugins.metro)
}
```

## Integration / E2E Tests

Use `exclude` on `@DependencyGraph` to remove production providers:

```kotlin
@DependencyGraph(
  AppScope::class,
  exclude = [ProductionNetworkProviders::class],
)
abstract class TestAppGraph {
  // Test providers will be used instead
}
```

**Future**: `replaces` on `ContributesTo` and `ContributesBinding` for easier test replacement.

## Fakes

Create fake providers:

```kotlin
// fake/
class FakeUserRepository : UserRepository {
  var dataToReturn: User? = null
  override suspend fun getUser() = dataToReturn ?: error("No data")
}

// tests/
@ContributesTo(AppScope::class)
interface TestUserProviders {
  @Provides fun fakeRepo(): UserRepository = FakeUserRepository()
}
```

## Tips

- Keep test setup simple
- Use fakes over mocks
- Only use full DI when necessary
- Test-specific graphs for complex scenarios
