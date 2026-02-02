# Naming and Structure

## Composable Naming

**Use PascalCase** to distinguish composables from regular functions:

```kotlin
// Good
@Composable
fun UserProfile(name: String, modifier: Modifier = Modifier) { }

// Bad - lowercase
@Composable
fun userProfile(name: String) { }
```

**Exception**: Value-returning functions may use camelCase.

## Function Types

### Content Emitters
Emit UI elements (e.g., `Text`, `Button`, `UserProfile`).

**Must NOT return values**:

```kotlin
// Good
@Composable
fun ProfileCard(user: User, modifier: Modifier = Modifier) {
  Card(modifier) { Text(user.name) }
}

// Bad - returning value from content emitter
@Composable
fun ProfileCard(user: User): User {
  Card { Text(user.name) }
  return user
}
```

### Non-emitting Composables
Return values or manage state (e.g., `remember`, `rememberSaveable`).

## Visibility

| Context                                                   | Visibility | Location      |
|-----------------------------------------------------------|------------|---------------|
| Top-level screen View                                     | `internal` | View.kt       |
| Reusable UI component                                     | `internal` | Separate file |
| Small (< 15 lines) child composable, tightly coupled      | `private`  | Same file     |
| Large (>= 15 lines) or independently useful child         | `internal` | Separate file |
| Preview composables                                       | `private`  | Same file     |

```kotlin
// Top-level screen View - internal
@Composable
internal fun MyScreenView(
  state: MyScreenViewState,
  onIntent: (MyScreenIntent) -> Unit,
) { }

// Small, tightly coupled child - private in same file
@Composable
private fun MyScreenTitle(title: String) { }

// Larger or independent child - internal in separate file (compose/ package for screens)
// compose/MyScreenHeader.kt
@Composable
internal fun MyScreenHeader(
  onBackClick: () -> Unit,
  title: String,
) { }
```

**Guidelines for extraction:**

- **Small** (< 15 lines, not counting the Composable function's parameters) and **tightly coupled**: Keep `private` in same file
- **Larger** (>= 15 lines) or **independently useful**: Extract as `internal` in separate file
- For screens: Place extracted composables in `compose/` package within screen module
- For shared components: Place in appropriate `ui/` module
- Extracted composables can have their own `private` helper composables

## CompositionLocal Naming

**Must be prefixed with `Local`**:

```kotlin
// Good
val LocalUserSession = compositionLocalOf<UserSession> { error("...") }

// Bad
val UserSession = compositionLocalOf<UserSession> { error("...") }
```

Use CompositionLocals sparingly. Prefer explicit parameters.

## Named Arguments

**Use named arguments on separate lines with 2+ parameters**:

```kotlin
// Good
MyScreen(
  onDismissError = {},
  onNavigateBack = {},
  state = state,
)

// Bad - positional unclear
MyScreen({}, {}, state)
```

**You may use named arguments on separate lines with 1 parameter**:

```kotlin
// Good
MyTitle(title)

// Good
MyTitle(
  title = title,
)
```
