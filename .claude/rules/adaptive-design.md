---
paths:
  - "**/*View.kt"
  - "**/*Screen*.kt"
  - "**/compose/**/*.kt"
  - "**/ui/**/*.kt"
---

# Adaptive Design

All UI work must account for different device form factors. This is not a "polish later" concern - it must be considered from the start of every UI implementation.

## Form Factors

| Form Factor | Window Width | Navigation        | Layout Considerations             |
|-------------|--------------|-------------------|-----------------------------------|
| Phone       | < 600dp      | Bottom navigation | Single column, full-width content |
| Tablet      | 600-840dp    | Navigation rail   | Two-column layouts, master-detail |
| Desktop     | > 840dp      | Navigation drawer | Multi-column, keyboard shortcuts  |

## Required Considerations

When implementing ANY screen or UI component:

1. **Layout Adaptation** - Consider how content reflows at different widths
2. **Navigation Integration** - Work with adaptive navigation scaffold
3. **Touch Targets** - Minimum 48dp on all platforms
4. **Grid Density** - Adjust column count based on available width
5. **Input Methods** - Touch, mouse, keyboard navigation support

## Window Size Classes

Use Material 3 window size classes for breakpoints:

```kotlin
// Detect current window size class
val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

when (windowSizeClass.windowWidthSizeClass) {
  WindowWidthSizeClass.COMPACT -> { /* Phone layout */ }
  WindowWidthSizeClass.MEDIUM -> { /* Tablet layout */ }
  WindowWidthSizeClass.EXPANDED -> { /* Desktop layout */ }
}
```

## Adaptive Navigation

This project uses `material3-adaptive-navigation3` from Compose Multiplatform:

```kotlin
implementation(libs.compose.nav3.adaptive)
```

## UI Implementation Checklist

Every UI PR should verify:

- [ ] Tested on phone-sized window (< 600dp width)
- [ ] Tested on tablet-sized window (600-840dp width)
- [ ] Tested on desktop-sized window (> 840dp width)
- [ ] Grid/list adapts column count appropriately
- [ ] Navigation works with adaptive scaffold
- [ ] Touch targets meet minimum 48dp size
- [ ] Keyboard navigation works (desktop/web)

## Common Patterns

### Adaptive Grid Columns

```kotlin
val columns = when (windowSizeClass.windowWidthSizeClass) {
  WindowWidthSizeClass.COMPACT -> 2
  WindowWidthSizeClass.MEDIUM -> 4
  WindowWidthSizeClass.EXPANDED -> 6
  else -> 2
}

LazyVerticalGrid(
  columns = GridCells.Fixed(columns),
  // ...
)
```

### Master-Detail Layout

For lists that navigate to details (library browsing, search results):
- **Compact:** Navigate to separate detail screen
- **Medium/Expanded:** Show list and detail side-by-side

### Documentation Reference

For complete patterns: [.docs/compose/adaptive-design.md](/.docs/compose/adaptive-design.md)
