---
name: sqldelight
description: Work with SQLDelight database - create tables, write queries, debug issues, add migrations, or understand schema patterns.
argument-hint: "[task] - e.g., 'create Payment table', 'add index', 'migrate User schema'"
context: fork
allowed-tools: Read, Edit, Write, Bash(./format, .scripts/run-gradle *, grep, cat), Glob, Grep
---

# SQLDelight Skill

Work with SQLDelight database - create tables, write queries, debug issues, add migrations, or understand existing schema.

## Common Tasks

```
/sqldelight create Payment table          # Create new table with queries
/sqldelight add index to Message          # Add index for performance
/sqldelight migrate User add email        # Add column migration
/sqldelight debug User query performance  # Analyze slow queries
/sqldelight explain Message schema        # Understand existing schema
/sqldelight write join query for messages    # Help with complex queries
```

## Schema Location

`services/database/impl/src/commonMain/sqldelight/com/eygraber/jellyfin/services/database/`

## Key Patterns

- **Custom types**: Use `TEXT AS MyId` with column adapters
- **Upsert**: `INSERT ... ON CONFLICT(id) DO UPDATE SET ...`
- **Indexes**: Create on foreign keys and frequently queried columns
- **Flows**: Use `.asFlow().mapToList()` in data sources
- **Transactions**: Use `db.transaction` for writes and `db.transactionWithResult` when the transaction returns a value

## Quick Reference

```sql
-- Basic table
CREATE TABLE Payment(
  id TEXT AS PaymentId PRIMARY KEY NOT NULL,
  userId TEXT AS UserId NOT NULL REFERENCES User(id) ON DELETE CASCADE,
  amount INTEGER NOT NULL
);

-- Index
CREATE INDEX IX_Payment_userId ON Payment(userId);

-- Upsert query
upsert:
INSERT INTO Payment(id, userId, amount)
VALUES(:id, :userId, :amount)
ON CONFLICT(id) DO UPDATE SET amount = :amount;
```

## Additional Resources

- [schema-patterns.md](schema-patterns.md) - Table definitions, types, indexes
- [query-patterns.md](query-patterns.md) - Common query patterns
- [migrations.md](migrations.md) - Schema migration strategies
