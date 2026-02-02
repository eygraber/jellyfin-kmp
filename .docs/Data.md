# Data Layer

See [data/](data/) for comprehensive data layer documentation:
- [data/repositories.md](data/repositories.md) - Repository pattern
- [data/datastore.md](data/datastore.md) - DataStore usage

## Quick Reference

```
data/<feature>/
+-- public/     # Interfaces, entities
+-- impl/       # Real implementations
+-- fake/       # Test doubles
```

| Component        | Purpose                           |
|------------------|-----------------------------------|
| Repository       | Coordinates local/remote, caching |
| LocalDataSource  | Database operations               |
| RemoteDataSource | API calls                         |
