# UI Layer

> The role of the UI is to display the application data on the screen and also to
> serve as the primary point of user interaction <sup>[1]</sup>

## Detailed Documentation

See [compose/](compose/) for Compose style guide and best practices:
- [compose/naming-structure.md](compose/naming-structure.md) - Naming conventions
- [compose/modifiers.md](compose/modifiers.md) - Modifier patterns
- [compose/state-management.md](compose/state-management.md) - State handling
- [compose/previews.md](compose/previews.md) - Preview configuration

See [architecture/vice-pattern.md](architecture/vice-pattern.md) for VICE framework details.

## Module Generation

Run `.scripts/generate_module` to generate a new screen module:

```bash
.scripts/generate_module --feature=<FeatureName>
```

## Screens

Each screen uses the [VICE] framework and is encapsulated in its own module under `screens/`.

**Components**:
- `ViceNavEntryProvider` - Screen entry point
- `ViewState` - Immutable UI state
- `Intent` - User actions
- `Compositor` - Routes intents, composites state
- `Model` - Business logic (ViceSource)
- `View` - Composable UI

The [ScreenGraph] creates a subgraph providing the navigation key and navigator.

## Navigation

[AndroidX Navigation3] is used together with `vice-nav3` to provide navigation.

The `nav` module contains `TemplateNav` composable with a `NavDisplay` as the navigation root.

**Key principle**: `NavBackStack` is NOT exposed outside `nav`. Screens receive navigation as lambdas:

```kotlin
class MyScreenNavigator(
  private val onBack: () -> Unit,
  private val onNavigateToDetails: (id: String) -> Unit,
)
```

This [encapsulates navigation] and prevents leaking library implementation details into screens.

See [architecture/navigation.md](architecture/navigation.md) for complete navigation patterns.

[1]: https://developer.android.com/topic/architecture/ui-layer
[AndroidX Navigation3]: https://developer.android.com/guide/navigation/navigation-3
[encapsulates navigation]: https://developer.android.com/guide/navigation/design/encapsulate
[ScreenGraph]: ./DI.md#template-di
[VICE]: https://github.com/eygraber/vice
