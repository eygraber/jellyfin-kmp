---
name: repository
description: Work with data repositories - create, modify, debug, or understand repository patterns, data sources, and data flow architecture.
argument-hint: "[task] - e.g., 'create User', 'debug sync', 'explain data flow'"
context: fork
allowed-tools: Read, Edit, Write, Bash(./format, ./gradlew *, grep, cat, mkdir, find), Glob, Grep
---

# Repository Skill

Work with data layer repositories - create new ones, modify existing ones, debug data flow, or understand patterns.

## Common Tasks

```
/repository create User            # Create new repository
/repository debug user sync issues # Debug data synchronization
/repository explain data flow      # Understand how data flows
/repository add method to Repo     # Modify existing repository
/repository fix retry logic        # Fix specific issues
```

## Architecture Overview

```
data/{module-name}/
+-- public/   -> Repository interface, data classes
+-- impl/     -> RealRepository, RemoteDataSource, LocalDataSource, API
+-- fake/     -> FakeRepository for testing
```

**Data flow:** Remote API -> Repository -> LocalDataSource -> Flow -> UI

## Key Patterns

- **Return types**: `Flow<T>` for observation, result types for operations
- **Retry**: Use retry policies for remote calls
- **DI**: `@ContributesBinding(AppScope::class)` for Metro
- **Sync pattern**: Fetch remote -> save local -> emit via Flow

## When Creating New Repositories

1. Check if module exists at `data/{module-name}/`
2. Create module structure if needed (see [module-structure.md](module-structure.md))
3. Create Repository interface in public (see [repository-patterns.md](repository-patterns.md))
4. Create RealRepository in impl
5. Create data sources in impl (see [data-sources.md](data-sources.md))
6. Create FakeRepository in fake (see [fake-patterns.md](fake-patterns.md))
7. Run `./format` and verify build

## Additional Resources

- [module-structure.md](module-structure.md) - Build files and directory setup
- [repository-patterns.md](repository-patterns.md) - Interface and implementation patterns
- [data-sources.md](data-sources.md) - RemoteDataSource and LocalDataSource
- [fake-patterns.md](fake-patterns.md) - Test double patterns
