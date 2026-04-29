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
internal class RealUserRepository(...) : UserRepository
```

**Equivalent manual code**:
```kotlin
interface UserRepositoryProviders {
  @Provides fun RealUserRepository.bind(): UserRepository = this
}
```

## Visibility

`@ContributesBinding` implementations should be `internal`. Metro's `generateContributionProviders`
generates top-level `@Provides` declarations that expose only the bound type, so the implementation
class doesn't need to be visible outside its module.

`@ContributesTo` interfaces must remain `public` - Metro needs to see them for graph aggregation.

**Pattern**:
- `public/` module has interfaces
- `impl/` module has `internal` `@ContributesBinding` implementations
- `app` module sees interfaces; Metro handles wiring

## Gradle Module Structure

```kotlin
// data/user/public - interface
interface UserRepository {
  suspend fun getUser(): User
}

// data/user/impl - implementation
@ContributesBinding(AppScope::class)
internal class RealUserRepository(
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
