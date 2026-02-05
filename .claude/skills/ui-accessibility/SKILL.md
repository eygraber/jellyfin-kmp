---
name: ui-accessibility
description: Ensure UI components meet accessibility standards - content descriptions, touch targets, screen readers.
user-invocable: false
---

# UI Accessibility

Quick reference for accessibility patterns in Compose.

## Content Descriptions

```kotlin
// Icons that convey meaning
Icon(
  imageVector = SuperDoIcons.Delete,
  contentDescription = stringResource(R.string.delete_item),
)

// Decorative icons (no description needed)
Icon(
  imageVector = SuperDoIcons.ChevronRight,
  contentDescription = null,
)
```

## Touch Targets

Minimum touch target: 48dp x 48dp

```kotlin
IconButton(
  onClick = { /* ... */ },
  modifier = Modifier.size(48.dp),
) {
  Icon(
    imageVector = SuperDoIcons.Close,
    contentDescription = stringResource(R.string.close),
    modifier = Modifier.size(24.dp),
  )
}
```

## Semantic Merging

```kotlin
// Merge clickable row into single accessibility node
Row(
  modifier = Modifier
    .clickable { /* ... */ }
    .semantics(mergeDescendants = true) { }
) {
  Text("Title")
  Text("Subtitle")
}
```

## Headings

```kotlin
Text(
  text = "Section Title",
  modifier = Modifier.semantics { heading() },
  style = MaterialTheme.typography.titleMedium,
)
```

## State Descriptions

```kotlin
Checkbox(
  checked = isChecked,
  onCheckedChange = { /* ... */ },
  modifier = Modifier.semantics {
    stateDescription = if (isChecked) "Selected" else "Not selected"
  }
)
```

## Documentation

- [.docs/compose/accessibility.md](/.docs/compose/accessibility.md) - Complete guide
