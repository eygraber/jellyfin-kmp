---
name: sqldelight
description: Work with SQLDelight database schemas, queries, and migrations.
argument-hint: "[task] - e.g., 'create users table', 'add migration', 'explain query'"
context: fork
allowed-tools: Read, Edit, Write, Bash(./gradlew *, grep, cat, mkdir, find), Glob, Grep
---

# SQLDelight Skill

Work with SQLDelight database schemas, queries, and migrations.

## Common Tasks

```
/sqldelight create users table         # Create new table
/sqldelight add migration for v5       # Create migration file
/sqldelight explain query pattern      # Understand query patterns
/sqldelight fix foreign key issue      # Debug schema problems
```

## File Structure

```
services/sqldelight/src/commonMain/sqldelight/
├── com/template/db/
│   ├── User.sq           # Table definition + queries
│   └── migrations/
│       └── 1.sqm         # Migration files
```

## Table Definition (.sq)

```sql
CREATE TABLE User (
  id TEXT NOT NULL PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT NOT NULL,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
);

CREATE INDEX user_email ON User(email);

-- Queries
selectAll:
SELECT * FROM User;

selectById:
SELECT * FROM User WHERE id = ?;

upsert:
INSERT OR REPLACE INTO User(id, name, email) VALUES (?, ?, ?);

deleteById:
DELETE FROM User WHERE id = ?;
```

## Migration Files (.sqm)

```sql
-- 1.sqm: Add email column
ALTER TABLE User ADD COLUMN email TEXT NOT NULL DEFAULT '';

-- Or for complex changes (copy-drop-rename):
CREATE TABLE User_new (
  id TEXT NOT NULL PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT NOT NULL
);

INSERT INTO User_new SELECT id, name, '' FROM User;
DROP TABLE User;
ALTER TABLE User_new RENAME TO User;
```

## Key Patterns

### Reserved Keywords
- Escape reserved words with backticks: `` `Case` ``, `` `Order` ``

### Foreign Keys
- Always add ON DELETE clause
- Cover FK columns with an index

```sql
CREATE TABLE Message (
  id TEXT NOT NULL PRIMARY KEY,
  user_id TEXT NOT NULL REFERENCES User(id) ON DELETE CASCADE
);

CREATE INDEX message_user_id ON Message(user_id);
```

### Query Execution

```kotlin
// Read (single)
withDbReadContext(dispatchers) { queries.selectById(id).executeAsOneOrNull() }

// Read (list)
withDbReadContext(dispatchers) { queries.selectAll().executeAsList() }

// Write
db.withTransaction(dispatchers) { queries.upsert(id, name, email) }

// Observe
queries.selectAll().asFlow().mapToList(dispatchers)
```

## Regenerating Code

After changing `.sq` files:
```bash
./gradlew :services:sqldelight:assembleDebug
```

## Documentation

- [.docs/data/sqldelight.md](/.docs/data/sqldelight.md) - Complete patterns
