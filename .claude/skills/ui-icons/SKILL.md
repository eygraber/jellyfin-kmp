---
name: ui-icons
description: Work with icons - add new icons, understand icon patterns, use SuperDoIcons.
user-invocable: false
---

# UI Icons

Quick reference for icon patterns.

## Using Icons

🔴 **Always use SuperDoIcons, never Material Icons library**

```kotlin
// GOOD
import com.com.superdo.ui.icons.SuperDoIcons

Icon(
  imageVector = SuperDoIcons.Settings,
  contentDescription = stringResource(R.string.settings),
)

// BAD - Never use Material Icons
import androidx.compose.material.icons.Icons
Icon(imageVector = Icons.Default.Settings, ...)
```

## Adding New Icons

1. Get SVG from design or Material Icons
2. Use Valkyrie IDE plugin to convert SVG → ImageVector
3. Add to `ui/icons` module as extension on `SuperDoIcons`

```kotlin
// ui/icons/src/main/kotlin/.../SuperDoIcons.kt
object SuperDoIcons

// ui/icons/src/main/kotlin/.../Settings.kt
val SuperDoIcons.Settings: ImageVector
  get() = materialIcon(name = "Settings") {
    // ... vector path data
  }
```

## Icon Guidelines

- Use `contentDescription` for meaningful icons
- Use `contentDescription = null` for decorative icons
- Ensure 48dp minimum touch target when icon is clickable
- Use appropriate icon size (typically 24dp for toolbar, 16dp for inline)

## Documentation

- [.claude/rules/ui-resources.md](/.claude/rules/ui-resources.md) - UI resource rules
