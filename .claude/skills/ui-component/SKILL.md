---
name: ui-component
description: Create reusable UI components in ui/ modules with proper patterns, previews, and tests.
argument-hint: "[component] - e.g., 'create LoadingButton', 'add UserAvatar'"
context: fork
allowed-tools: Read, Edit, Write, Glob, Grep, Bash(./format, ./gradlew *, mkdir)
---

# UI Component Skill

Create reusable UI components in `ui/` modules.

## Common Tasks

```
/ui-component create LoadingButton     # Create new component
/ui-component add UserAvatar           # Add avatar component
/ui-component explain patterns         # Understand component patterns
```

## When to Create UI Components

**Create in `ui/` when:**
- Component is used by 2+ screen modules
- Component is a design system element

**Keep in screen module when:**
- Only used by one screen
- Screen-specific behavior

## Component Pattern

```kotlin
@Composable
fun LoadingButton(
  onClick: () -> Unit,                    // Required callbacks first
  text: String,                           // Required params
  modifier: Modifier = Modifier,          // Modifier
  isLoading: Boolean = false,             // Optional params
  enabled: Boolean = true,
) {
  Button(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled && !isLoading,
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(16.dp),
        strokeWidth = 2.dp,
      )
    } else {
      Text(text = text)
    }
  }
}

@PreviewJellyfinScreen
@Composable
private fun LoadingButtonPreview(
  @PreviewParameter(LoadingButtonPreviewProvider::class)
  state: LoadingButtonState,
) {
  JellyfinPreviewTheme {
    LoadingButton(
      onClick = {},
      text = state.text,
      isLoading = state.isLoading,
    )
  }
}

private class LoadingButtonPreviewProvider : NamedPreviewParameterProvider<LoadingButtonState>(
  "default" to LoadingButtonState("Submit"),
  "loading" to LoadingButtonState("Submit", isLoading = true),
  "disabled" to LoadingButtonState("Submit", enabled = false),
)

private data class LoadingButtonState(
  val text: String,
  val isLoading: Boolean = false,
  val enabled: Boolean = true,
)
```

## Module Structure

```
ui/<component-type>/
├── src/commonMain/kotlin/com/jellyfin/ui/<type>/
│   └── LoadingButton.kt
└── src/commonTest/kotlin/com/jellyfin/ui/<type>/
    └── LoadingButtonScreenshotTest.kt
```

## Screenshot Tests

See `/screenshot-tests` skill for testing UI components.

## Documentation

- [.docs/compose/](/.docs/compose) - Compose patterns
- [.claude/rules/compose.md](/.claude/rules/compose.md) - Compose rules
