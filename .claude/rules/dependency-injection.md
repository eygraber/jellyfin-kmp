---
paths:
  - "**/*Graph*.kt"
  - "**/*Module*.kt"
---

Use Metro for dependency injection
Never use Dagger, Hilt, or Koin - use Metro only

# Scope Hierarchy
AppScope → ActivityScope → NavScope → ScreenScope
AppScope → WorkScope → Worker-specific scopes

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

# WorkManager
For workers, use worker class itself as scope:
@GraphExtension(MyWorker::class)
See workmanager.md for complete patterns

# Context Injection
Use @AppContext for most Context needs (repositories, models, services, WorkManager)
Use @ActivityContext only when Activity context specifically required (rare)
Never use LocalContext.current in Models, Compositors, or business logic - always inject via DI

❌ Bad: val context = LocalContext.current in Model
✅ Good: class MyModel(@AppContext private val context: Context)

# Testing
Prefer manual DI or test-specific components over production DI in tests

### Documentation Reference
For complete patterns: .docs/di/
For Context injection: .docs/di/qualifiers.md
For WorkManager: .docs/di/workmanager.md
