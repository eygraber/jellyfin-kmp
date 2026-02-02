# DI Providers

## ContributesTo

Automatically adds providers interface to a graph:

```kotlin
@ContributesTo(AppScope::class)
interface FooProviders {
  @Provides fun provideFoo(): Foo = Foo()
}
```

**Without Metro**:
```kotlin
// Would need to manually implement
@DependencyGraph
abstract class AppGraph : FooProviders, BarProviders, BazProviders
```

**With Metro**:
Code generation handles the wiring.

## ContributesBinding

Automatically binds implementation to interface:

```kotlin
interface UserRepository

@ContributesBinding(AppScope::class)
class RealUserRepository(...) : UserRepository
```

**Equivalent manual code**:
```kotlin
interface UserRepositoryProviders {
  @Provides fun RealUserRepository.bind(): UserRepository = this
}
```

## Visibility Requirements

The Gradle module containing the DependencyGraph must see types using these annotations.

If it can't see them, code generation fails.

**Pattern**:
- `public/` module has interfaces
- `impl/` module has `@ContributesBinding` implementations
- `app` module sees both and creates graphs

## Gradle Module Structure

```kotlin
// data/user/public - interface
interface UserRepository {
  suspend fun getUser(): User
}

// data/user/impl - implementation
@ContributesBinding(AppScope::class)
class RealUserRepository(
  private val api: UserApi,
  private val db: UserDatabase,
) : UserRepository {
  override suspend fun getUser(): User = ...
}
```

## Providing Dependencies

```kotlin
@ContributesTo(AppScope::class)
interface NetworkProviders {
  @Provides
  fun provideHttpClient(): HttpClient = HttpClient { ... }
}
```
