---
name: ui-icons
description: Work with icons - add new icons, understand icon patterns, use JellyfinIcons.
user-invocable: false
---

# UI Icons

Quick reference for icon patterns.

## Using Icons

🔴 **Always use JellyfinIcons, never Material Icons library**

```kotlin
// GOOD
import com.com.eygraber.jellyfin.ui.icons.JellyfinIcons

Icon(
  imageVector = JellyfinIcons.Settings,
  contentDescription = stringResource(R.string.settings),
)

// BAD - Never use Material Icons
import androidx.compose.material.icons.Icons
Icon(imageVector = Icons.Default.Settings, ...)
```

## Adding New Icons

1. Get SVG from design or Material Icons
2. Use Valkyrie IDE plugin to convert SVG → ImageVector
3. Add to `ui/icons` module as extension on `JellyfinIcons`

```kotlin
// ui/icons/src/commonMain/kotlin/.../JellyfinIcons.kt
object JellyfinIcons

// ui/icons/src/commonMain/kotlin/.../Settings.kt
val JellyfinIcons.Settings: ImageVector
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
