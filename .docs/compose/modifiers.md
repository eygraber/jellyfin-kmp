# Modifier Usage

## Required Parameter

**Every public composable that emits UI should accept a Modifier**:

```kotlin
// Good
@Composable
fun UserCard(user: User, modifier: Modifier = Modifier) {
  Card(modifier = modifier) { /* ... */ }
}

// Bad - no modifier
@Composable
fun UserCard(user: User) {
  Card { /* ... */ }
}
```

**Exceptions**: `internal`/`private` composables or highly domain-specific functions.

## Guidelines

1. **Name it `modifier`** - always lowercase
2. **Default to `Modifier`** - `modifier: Modifier = Modifier`
3. **Position as first optional parameter** - after required, before other optional
4. **Pass to root composable** - apply to top-level element
5. **Don't create modifiers inside composables** - pass them in
6. **Waivers** - these guidelines can be sparingly waived for exception reasons in specific scenarios (e.g. performance)

## Parameter Ordering

```kotlin
@Composable
fun ProfileCard(
  onClick: () -> Unit,               // Required lambda params first
  name: String,                      // Required params next
  email: String,                     // Required params next
  modifier: Modifier = Modifier,     // Modifier next
  onUpdated: () -> Unit = {},        // Optional lambda params next
  label: String? = null,             // Optional params next
  content: @Composable () -> Unit    // Primary content lambda last
) { }
```

## Never Reuse Modifiers

```kotlin
// Bad - reusing modifier
@Composable
fun MyComposable(modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    Text("First", modifier = modifier)    // Reused!
    Text("Second", modifier = modifier)   // Reused!
  }
}

// Good - each gets its own
@Composable
fun MyComposable(modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    Text("First")
    Text("Second")
  }
}
```
