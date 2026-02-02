---
paths:
  - "**/*Graph*.kt"
  - "**/*Module*.kt"
---

Use Metro for dependency injection
Never use Dagger, Hilt, or Koin - use Metro only

# Scope Hierarchy
AppScope -> ActivityScope -> NavScope -> ScreenScope

# Key Annotations
@SingleIn(AppScope::class) - Scoped singleton
@ContributesBinding(AppScope::class) - Bind implementation to interface (no @Inject needed)
@ContributesTo(AppScope::class) - Contribute providers to graph

# Screen Graphs
@GraphExtension(ScreenScope::class)
interface MyScreenGraph {
  val navEntryProvider: MyScreenNavEntry
  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory { fun create(...): MyScreenGraph }
}

# Gradle Setup
plugins { alias(libs.plugins.metro) }

# Testing
Prefer manual DI or test-specific components over production DI in tests

### Documentation Reference
For complete patterns: .docs/di/
