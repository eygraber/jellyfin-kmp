# Architecture

Template's architecture is inspired by [Android App Architecture], consisting of a [UI layer], [Domain layer], and [Data layer], adapted for Kotlin Multiplatform.

## Principles

- Immutability
- Well-defined boundaries
- Unidirectional data flow (UDF)

## Detailed Documentation

See [architecture/](architecture/) for comprehensive documentation:
- [architecture/vice-pattern.md](architecture/vice-pattern.md) - VICE MVI framework
- [architecture/layers.md](architecture/layers.md) - Layer responsibilities
- [architecture/navigation.md](architecture/navigation.md) - Navigation patterns

## Platform Entry Points

### Android

Lives in `apps/android` module:
- `TemplateApplication` - Holds DI AppGraph, initialization delegated to `TemplateInitializer`
- `TemplateActivity` - Single Activity, Compose entry point, holds ActivityGraph

### iOS

Lives in `apps/ios` module with iOS-specific entry points.

### Desktop

Lives in `apps/desktop` module with JVM desktop entry point.

### Web

Lives in `apps/web` module with WasmJs entry point.

## Screens

Screens are built using [VICE] and define:
- `ViceNavEntryProvider` - Screen entry point
- `ViewState` - Immutable UI state
- `Intent` - User actions
- `Compositor` - Routes intents, composites state
- `View` - Composable UI

They live in modules under the `screens` directory.

See [UI documentation](./UI.md) and [architecture/vice-pattern.md](architecture/vice-pattern.md) for details.

## Services

External library integrations live in `services/` modules.

[Android App Architecture]: https://developer.android.com/topic/architecture
[Data layer]: https://developer.android.com/topic/architecture/data-layer
[Domain layer]: https://developer.android.com/topic/architecture/domain-layer
[UI layer]: https://developer.android.com/topic/architecture/ui-layer
[VICE]: https://github.com/eygraber/vice
