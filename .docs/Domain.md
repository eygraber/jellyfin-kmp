# Domain Layer

See [domain/README.md](domain/README.md) for comprehensive domain layer documentation.

## When to Use

Extract logic to domain layer when:
- Shared by multiple screens or features
- Complex business rules need enforcement
- Data aggregation from multiple sources needed

**Don't create domain layer for**:
- Logic used by only one screen (keep in screen's Model)
- Simple data transformations
- UI-specific logic

## Module Structure

```
domain/<feature>/
+-- public/     # Use case interfaces and models
+-- impl/       # Real implementations
+-- fake/       # Test doubles
```
