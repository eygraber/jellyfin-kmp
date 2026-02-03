---
name: repository
description: Work with data repositories - create, modify, debug, or understand repository patterns, data sources, and data flow architecture.
argument-hint: "[task] - e.g., 'create Payment', 'debug user sync', 'explain data flow'"
context: fork
allowed-tools: Read, Edit, Write, Bash(./format, ./gradlew *, grep, cat, mkdir, find), Glob, Grep
---

# Repository Skill

Work with data layer repositories - create new ones, modify existing ones, debug data flow, or understand patterns.

## Common Tasks

```
/repository create PaymentTransaction   # Create new repository
/repository debug user sync issues      # Debug data synchronization
/repository explain data flow           # Understand how data flows
/repository add method to UserRepository # Modify existing repository
/repository fix retry logic in messages # Fix specific issues
```

## Architecture Overview

```
data/{module-name}/
├── public/   → Repository interface, data classes
├── impl/     → RealRepository, RemoteDataSource, LocalDataSource, API
└── fake/     → FakeRepository for testing
```

**Data flow:** Remote API → Repository → LocalDataSource → SQLDelight → Flow → UI

## Key Patterns

- **Return types**: `Flow<T>` for observation, `TemplateResult<T>` for operations
- **Retry**: Use `retryTemplateResult(retryPolicy)` for remote calls
- **DI**: `@ContributesBinding(AppScope::class)` for Metro
- **Sync pattern**: Fetch remote → save local → emit via Flow

## When Creating New Repositories

1. Check if module exists at `data/{module-name}/`
2. Create module structure if needed
3. Create Repository interface in public
4. Create RealRepository in impl
5. Create data sources in impl
6. Create FakeRepository in fake
7. Run `./format` and verify build

## Related Skills

- **[datastore](../datastore/SKILL.md)** - Working with the DataStore library
- **[ktorfit](../ktorfit/SKILL.md)** - API communication, RemoteDataSource, JSON parsing
- **[sqldelight](../sqldelight/SKILL.md)** - Database schema, LocalDataSource, queries
