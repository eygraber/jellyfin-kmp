# Dependency Injection

Metro for compile-time DI.

## Contents

- [scopes.md](scopes.md) - AppScope, ScreenScope hierarchy
- [providers.md](providers.md) - ContributesTo, ContributesBinding
- [testing.md](testing.md) - Test components and fakes
- [alternatives.md](alternatives.md) - Why not Dagger/Hilt/Koin

## Key Library

- [Metro](https://github.com/ZacSweers/metro) - Native Kotlin DI with compile-time safety

## Why Metro?

- Native Kotlin support
- Uses Kotlin compiler plugins (faster than KSP/KAPT)
- Full compile-time safety
- Simpler API than Dagger
- Combines best of Dagger and kotlin-inject

## Terminology

| Term            | Meaning                                         |
|-----------------|-------------------------------------------------|
| Graph           | Class annotated with `@DependencyGraph`         |
| GraphExtension  | Child graph annotated with `@GraphExtension`    |
| Providers       | Interface with `@Provides` functions            |
| Scope           | Lifecycle boundary (App, Activity, Screen)      |

## Quick Reference

```kotlin
// Singleton in scope
@Inject
@SingleIn(AppScope::class)
class UserRepository(...)

// Contribute binding (no @Inject needed)
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class RealUserRepository(...) : UserRepository

// Contribute providers
@ContributesTo(AppScope::class)
interface MyProviders {
  @Provides fun provideFoo(): Foo = Foo()
}
```

## Gradle Setup

```kotlin
plugins {
  alias(libs.plugins.metro)
}
```
