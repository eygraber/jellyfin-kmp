# Architecture

Jellyfin follows [Android App Architecture](https://developer.android.com/topic/architecture) with UI, Domain, and Data layers, adapted for Kotlin Multiplatform.

## Core Principles

- Immutability
- Well-defined boundaries
- Unidirectional data flow (UDF)

## Contents

- [vice-pattern.md](vice-pattern.md) - MVI/VICE framework for screens
- [layers.md](layers.md) - UI, Domain, Data layer responsibilities
- [navigation.md](navigation.md) - Navigation3 and NavEntry patterns

## Quick Reference

| Layer  | Location          | Responsibility                 |
|--------|-------------------|--------------------------------|
| UI     | `screens/`, `ui/` | Display state, capture intents |
| Domain | `domain/`         | Business logic, use cases      |
| Data   | `data/`           | Repositories, data sources     |

See [ProjectLayout.md](/.docs/ProjectLayout.md) for module dependency graph.
