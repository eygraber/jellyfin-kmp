# Compose Multiplatform

Compose style guide and best practices for Jellyfin.

## Contents

- [naming-structure.md](naming-structure.md) - Naming conventions, function types
- [modifiers.md](modifiers.md) - Modifier patterns and rules
- [state-management.md](state-management.md) - State handling, remember, hoisting
- [previews.md](previews.md) - Preview annotations and parameters

## Essential Rules

| Rule                       | Example                                     |
|----------------------------|---------------------------------------------|
| PascalCase composables     | `fun UserProfile()` not `fun userProfile()` |
| Modifier parameter         | `modifier: Modifier = Modifier`             |
| Material 3 only            | `import androidx.compose.material3.*`       |
| Never pass `onIntent` down | Use specific callbacks                      |

## Enforcement

- **Detekt** with [compose-rules](https://github.com/mrmans0n/compose-rules)
- **Code review** for manual verification

## References

- [Compose API Guidelines](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md)
- [VICE Framework](https://github.com/eygraber/vice)
