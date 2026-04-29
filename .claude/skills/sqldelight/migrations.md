# Schema Migrations

## Migration File Location

`services/database/impl/src/commonMain/sqldelight/migrations/`

Files are named by version number: `1.sqm`, `2.sqm`, etc.

## Prefer ALTER TABLE

SQLite supports these `ALTER TABLE` operations:

```sql
-- Add column
ALTER TABLE Payment ADD COLUMN currency TEXT DEFAULT 'USD';

-- Rename column
ALTER TABLE Payment RENAME COLUMN desc TO description;

-- Drop column (SQLite 3.35+)
ALTER TABLE Payment DROP COLUMN deprecated_field;

-- Rename table
ALTER TABLE OldPayment RENAME TO Payment;
```

## Simple Migration Examples

**Adding a nullable column (1.sqm):**
```sql
ALTER TABLE Payment ADD COLUMN notes TEXT;
```

**Adding a column with default (2.sqm):**
```sql
ALTER TABLE Payment ADD COLUMN currency TEXT NOT NULL DEFAULT 'USD';
```

**Renaming a column (3.sqm):**
```sql
ALTER TABLE Payment RENAME COLUMN desc TO description;
```

## Complex Migration (Copy-Drop-Rename)

When `ALTER TABLE` isn't sufficient (type changes, constraint changes):

```sql
-- 4.sqm: Change amount from TEXT to INTEGER

-- Step 1: Create new table
CREATE TABLE Payment_new(
  id TEXT PRIMARY KEY NOT NULL,
  userId TEXT NOT NULL REFERENCES User(id) ON DELETE CASCADE,
  amount INTEGER NOT NULL,
  description TEXT,
  createdAt INTEGER NOT NULL
);

-- Step 2: Copy data with conversion
INSERT INTO Payment_new(id, userId, amount, description, createdAt)
SELECT id, userId, CAST(amount AS INTEGER), description, createdAt
FROM Payment;

-- Step 3: Drop old table
DROP TABLE Payment;

-- Step 4: Rename new table
ALTER TABLE Payment_new RENAME TO Payment;

-- Step 5: Recreate indexes
CREATE INDEX IX_Payment_userId ON Payment(userId);
CREATE INDEX IX_Payment_createdAt ON Payment(createdAt DESC);
```

## Adding a New Table

```sql
-- 5.sqm: Add PaymentMethod table

CREATE TABLE PaymentMethod(
  id TEXT PRIMARY KEY NOT NULL,
  userId TEXT NOT NULL REFERENCES User(id) ON DELETE CASCADE,
  type TEXT NOT NULL,
  last4 TEXT,
  isDefault INTEGER AS Boolean NOT NULL DEFAULT 0
);

CREATE INDEX IX_PaymentMethod_userId ON PaymentMethod(userId);
```

## Migration Guidelines

1. **Test migrations** against production-like data before release
2. **Consider default values** for new NOT NULL columns
3. **Preserve data** when possible - avoid dropping columns with important data
4. **Recreate indexes** after copy-drop-rename migrations
5. **Order matters** - migrations run sequentially by version number
