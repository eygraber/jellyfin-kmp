# Adaptive Design

This document describes the adaptive design strategy for supporting multiple device
form factors across all platforms (Android, iOS, Desktop, Web).

## Philosophy

**Adaptive design is not a polish phase - it's a core requirement from day one.**

Every screen and UI component must be designed with multiple form factors in mind.
This prevents costly rework and ensures a consistent experience across devices.

## Form Factor Targets

| Form Factor        | Window Width | Primary Input  | Navigation Style      | Example Devices        |
|--------------------|--------------|----------------|-----------------------|------------------------|
| Compact (Phone)    | < 600dp      | Touch          | Bottom navigation bar | Phones, small tablets  |
| Medium (Tablet)    | 600-840dp    | Touch          | Navigation rail       | Tablets, foldables     |
| Expanded (Desktop) | > 840dp      | Mouse/Keyboard | Permanent nav drawer  | Desktop, large tablets |

## Material 3 Adaptive Components

### Adaptive Navigation Suite (material3-adaptive-navigation-suite)

The `material3-adaptive-navigation-suite` library provides `NavigationSuiteScaffold`, which
automatically chooses between bottom navigation, navigation rail, and navigation drawer based on
window size:

```kotlin
implementation(libs.compose.material3.adaptiveNavigationSuite)
```

This library is wired up in the `nav` module via `JellyfinNavigationSuiteScaffold`, which uses the
[adaptive breakpoints below](#form-factor-targets) and hides the navigation suite entirely on
non-top-level destinations (onboarding, detail screens, etc.).

> **Why a separate artifact?** The `compose.nav3.adaptive` artifact
> (`org.jetbrains.compose.material3.adaptive:adaptive-navigation3`) only contains the
> `ListDetailSceneStrategy` / `SupportingPaneSceneStrategy` for multi-pane layouts. The
> `NavigationSuiteScaffold` API lives in the separate
> `material3-adaptive-navigation-suite` artifact and tracks the same version as the project's
> `composeMaterial3Jetbrains` (currently `1.11.0-alpha07`).

### Window Size Classes

Use Material 3 window size classes for responsive breakpoints:

```kotlin
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun AdaptiveContent() {
  val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

  when (windowSizeClass.windowWidthSizeClass) {
    WindowWidthSizeClass.COMPACT -> CompactLayout()
    WindowWidthSizeClass.MEDIUM -> MediumLayout()
    WindowWidthSizeClass.EXPANDED -> ExpandedLayout()
  }
}
```

## Layout Patterns

### Single-Pane vs Multi-Pane

| Window Size | Pattern                | Use Case                             |
|-------------|------------------------|--------------------------------------|
| Compact     | Single-pane            | Full navigation to each screen       |
| Medium      | List-detail (optional) | Side-by-side when content benefits   |
| Expanded    | List-detail (required) | Always show context alongside detail |

### Grid Column Adaptation

Grids should adapt column count based on available width:

```kotlin
@Composable
fun AdaptiveMediaGrid(
  items: List<MediaItem>,
  modifier: Modifier = Modifier,
) {
  val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

  val columns = when (windowSizeClass.windowWidthSizeClass) {
    WindowWidthSizeClass.COMPACT -> 2
    WindowWidthSizeClass.MEDIUM -> 4
    WindowWidthSizeClass.EXPANDED -> 6
    else -> 2
  }

  LazyVerticalGrid(
    columns = GridCells.Fixed(columns),
    modifier = modifier,
  ) {
    items(items) { item ->
      MediaCard(item)
    }
  }
}
```

### Navigation Integration

The app uses `JellyfinNavigationSuiteScaffold` (in the `nav` module) which wraps Material's
`NavigationSuiteScaffold`. The scaffold:

- Reads the current top-most key from the `NavBackStack`
- Resolves it to a `JellyfinTopLevelDestination` (Home, Search, ...) and marks it selected
- Replaces the entire back stack with the destination key when an item is tapped
- Renders **no navigation surface** when the top-most key is not a top-level destination
  (e.g. on the splash, welcome, or any detail screen) so onboarding and detail flows render
  full-bleed

The scaffold automatically switches between:
- **Bottom navigation** on compact windows (< 600dp)
- **Navigation rail** on medium windows (600-840dp)
- **Permanent navigation drawer** on expanded windows (> 840dp)

To add a new top-level destination, add an entry to `JellyfinTopLevelDestination` with the screen's
`NavKey`, an `ImageVector` icon, and a `StringResource` label.

## Input Method Considerations

### Touch

- Minimum touch target: 48dp x 48dp
- Adequate spacing between interactive elements
- Swipe gestures for common actions

### Mouse

- Hover states for interactive elements
- Right-click context menus where appropriate
- Scroll wheel support

### Keyboard

- Tab navigation through focusable elements
- Keyboard shortcuts for common actions
- Focus indicators visible and clear

```kotlin
@Composable
fun KeyboardNavigableItem(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clickable(onClick = onClick)
      .focusable()
      .onKeyEvent { event ->
        if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
          onClick()
          true
        } else {
          false
        }
      },
  ) {
    // Content
  }
}
```

## Testing Adaptive Layouts

### Manual Testing Checklist

For every UI PR:

1. **Resize window** through all breakpoints
2. **Test navigation** adapts correctly
3. **Verify touch targets** meet minimum size
4. **Test keyboard navigation** (desktop/web)
5. **Check content reflow** at each breakpoint

## Platform-Specific Considerations

### Android

- Support foldables and multi-window mode
- Test with different system font sizes
- Respect system navigation bar insets

### iOS

- Support iPad multitasking (split view, slide over)
- Respect safe area insets
- Support Dynamic Type

### Desktop

- Support window resizing
- Provide keyboard shortcuts
- Support high-density displays

### Web

- Support responsive browser resizing
- Ensure touch works on touchscreen laptops
- Test with keyboard-only navigation

## Related Documentation

- [Compose Rules](/.claude/rules/compose.md)
- [Adaptive Design Rules](/.claude/rules/adaptive-design.md)
- [Navigation](/.docs/architecture/navigation.md)
