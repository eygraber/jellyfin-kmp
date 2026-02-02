# Architecture Layers

## UI Layer

> The role of the UI is to display the application data on the screen and serve as the primary point of user interaction.

**Location**: `screens/`, `ui/`

**Responsibilities**:
- Display state via Compose
- Capture user intents
- NO business logic

**Key Components**:
- `ViceNavEntryProvider` - Screen entry point
- `View` composables - UI rendering
- `ViewState` - Immutable UI state

## Domain Layer

**Location**: `domain/<feature>/`

**Structure**:
- `public/` - Use case interfaces
- `impl/` - Real implementations
- `fakes/` - Test doubles

**Responsibilities**:
- Cross-module business logic
- Orchestrate data operations
- Transform data for UI consumption

**When to Use**:
- Models shared by multiple screens
- Complex business rules
- Data aggregation from multiple sources

```kotlin
interface GetUserDetailsUseCase {
  suspend operator fun invoke(userId: String): UserDetails
}

@Inject
@ContributesBinding(AppScope::class)
class RealGetUserDetailsUseCase(
  private val userRepository: UserRepository,
) : GetUserDetailsUseCase {
  override suspend fun invoke(userId: String): UserDetails {
    // Coordinate data from multiple repositories
  }
}
```

## Data Layer

**Location**: `data/<feature>/`

**Structure**:
- `public/` - Repository interfaces, entities
- `impl/` - Real implementations
- `fake/` - Test doubles

**Responsibilities**:
- Data access coordination
- Local/remote sync
- Caching strategy

**Key Components**:
- Repository - Coordinates local and remote
- LocalDataSource - Database operations
- RemoteDataSource - API operations

See [data/repositories.md](/.docs/data/repositories.md) for patterns.

## App Glue

### Android

Lives in `apps/android` module:

- `TemplateApplication` - Holds DI AppGraph
- `TemplateActivity` - Single Activity, Compose entry point
- `TemplateInitializer` - App initialization

### iOS

Lives in `apps/ios` module with iOS-specific entry points.

### Desktop

Lives in `apps/desktop` module with JVM desktop entry point.

### Web

Lives in `apps/web` module with WasmJs entry point.

## Services

**Location**: `services/<name>/`

Integrations with external libraries and platform-specific functionality:
- `device-sensors` - Device sensors integration
- `splash-screen` - Splash screen service
