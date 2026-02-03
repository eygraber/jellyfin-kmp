---
name: compose-patterns
description: Compose organization patterns - breaking up composables, parameter passing, file organization.
user-invocable: false
---

# Compose Patterns Reference

Quick reference for Compose organization patterns. Claude loads this automatically when working with Compose code.

## Breaking Up Components

### When to Extract

- **Extract**: Functions >= 15 lines, independently useful, or complex logic
- **Keep inline**: Functions < 15 lines, tightly coupled to parent

### Visibility Rules

| Situation                   | Visibility | File          |
|-----------------------------|------------|---------------|
| Small helper (< 15 lines)   | `private`  | Same file     |
| Larger helper (>= 15 lines) | `internal` | Separate file |
| Reusable across modules     | `public`   | Separate file |

## Parameter Passing

### Pass Specific Parameters

```kotlin
// GOOD - Pass only what's needed
@Composable
fun UserCard(
  name: String,
  email: String,
  modifier: Modifier = Modifier,
) {
  UserCardHeader(name = name)  // Only needs name
  UserCardBody(email = email)  // Only needs email
}

// BAD - Passing entire object
@Composable
fun UserCard(
  user: User,  // 20+ properties but only 2 needed
  modifier: Modifier = Modifier,
)
```

**Exception**: When object is `@Immutable`/`@Stable` and most fields are used.

## Parameter Order

1. Required callbacks (`onClick`, `onDismiss`)
2. Required data parameters
3. `modifier: Modifier = Modifier`
4. Optional parameters with defaults
5. Content lambda (always last)

## File Organization

```
components/
├── MyComponent.kt           # Main component
├── MyComponentDefaults.kt   # Defaults object (if complex)
└── internal/
    └── MyComponentHeader.kt # Extracted internal composables
```

## Preview Guidelines

- Add previews for extracted components in separate files
- Use `NamedPreviewParameterProvider` for multiple states

## Documentation

See [.docs/compose/](/.docs/compose/) for complete patterns:
- [composable-organization.md](/.docs/compose/composable-organization.md) - Breaking up composables
- [naming-structure.md](/.docs/compose/naming-structure.md) - Naming conventions
- [modifiers.md](/.docs/compose/modifiers.md) - Modifier patterns
- [previews.md](/.docs/compose/previews.md) - Preview configuration
