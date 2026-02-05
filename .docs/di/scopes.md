# DI Scopes

Scope hierarchy for Jellyfin:

```
JellyfinApplicationGraph
         |
         +---> JellyfinActivityGraph
         |          |
         |   JellyfinNavGraph
         |          |
         |   ScreenGraph
         |
         +---> WorkGraph (if applicable)
```

## AppScope

Root scope. Lives as long as the application.

**Graph**: `JellyfinApplicationGraph`

**Use for**:
- Repositories
- Global services
- Singletons

```kotlin
@Inject
@SingleIn(AppScope::class)
class UserRepository(...)
```

## ActivityScope

Lives as long as the Activity.

**Graph**: `JellyfinActivityGraph` (child of App)

**Use for**:
- Activity-scoped resources
- Navigation state

## NavScope

Lives as long as the navigation graph.

**Graph**: `JellyfinNavGraph` (child of Activity)

**Exists because**: `JellyfinActivityGraph` needs to stay in `apps/android` module while `nav` needs to access it.

## ScreenScope

1:1 relationship with a `ViceNavEntryProvider`.

**Graph**: `ScreenGraph` (child of Nav)

**Use for**:
- Screen-specific state
- Screen-local services

```kotlin
@GraphExtension(ScreenScope::class)
interface MyScreenGraph {
  val navEntryProvider: MyScreenNavEntry

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(
      @Provides key: MyScreenKey,
      @Provides navigator: MyScreenNavigator,
    ): MyScreenGraph
  }
}
```

## Scoped Singletons

Use `@SingleIn` to cache instances per scope:

```kotlin
// New instance each injection
@Inject
class Foo

// Cached in AppScope
@Inject
@SingleIn(AppScope::class)
class Foo

// Cached in ScreenScope
@Inject
@SingleIn(ScreenScope::class)
class Bar
```
