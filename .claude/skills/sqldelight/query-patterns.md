# Query Patterns

## Basic CRUD Queries

```sql
-- Count
count:
SELECT COUNT(*) FROM Payment;

-- Select all
selectAll:
SELECT * FROM Payment ORDER BY createdAt DESC;

-- Select by ID
selectById:
SELECT * FROM Payment WHERE id = :whereId;

-- Select with limit
selectRecent:
SELECT * FROM Payment ORDER BY createdAt DESC LIMIT :limit;

-- Delete by ID
delete:
DELETE FROM Payment WHERE id = :whereId;

-- Delete all
deleteAll:
DELETE FROM Payment;
```

## Upsert Pattern

```sql
-- Basic upsert
upsert:
INSERT INTO Payment(
  id,
  userId,
  amount,
  description,
  createdAt
)
VALUES(
  :id,
  :userId,
  :amount,
  :description,
  :createdAt
)
ON CONFLICT(id)
DO UPDATE SET
  userId = :userId,
  amount = :amount,
  description = :description;

-- Upsert with COALESCE (preserve existing if null passed)
upsertSelective:
INSERT INTO Payment(id, userId, amount, description, createdAt)
VALUES(:id, :userId, :amount, :description, :createdAt)
ON CONFLICT(id)
DO UPDATE SET
  amount = COALESCE(:amount, amount),
  description = COALESCE(:description, description);

-- Conditional upsert (update only specific field)
upsertStatus:
INSERT INTO Payment(id, userId, amount, status, createdAt)
VALUES(:id, :userId, :amount, :status, :createdAt)
ON CONFLICT(id)
DO UPDATE SET
  status = :status;
```

## Sync Patterns

```sql
-- Delete items not in sync response
deleteAllNotIn:
DELETE FROM Payment WHERE id NOT IN :idsToKeep;

-- Delete by foreign key
deleteByUserId:
DELETE FROM Payment WHERE userId = :userId;

-- Bulk delete
deleteByIds:
DELETE FROM Payment WHERE id IN :ids;
```

## Filtered Queries

```sql
-- Filter by status
selectByStatus:
SELECT * FROM Payment WHERE status = :status ORDER BY createdAt DESC;

-- Filter by multiple statuses
selectByStatuses:
SELECT * FROM Payment WHERE status IN :statuses ORDER BY createdAt DESC;

-- Filter by user
selectByUserId:
SELECT * FROM Payment WHERE userId = :userId ORDER BY createdAt DESC;

-- Combined filters
selectFiltered:
SELECT * FROM Payment
WHERE userId = :userId
  AND status = :status
  AND createdAt >= :since
ORDER BY createdAt DESC;
```

## Join Queries

```sql
-- Inner join
selectWithUser:
SELECT
  p.*,
  u.name AS userName
FROM Payment p
INNER JOIN User u ON p.userId = u.id
WHERE p.id = :paymentId;

-- Left join (nullable relationship)
selectWithCategory:
SELECT
  p.*,
  c.name AS categoryName
FROM Payment p
LEFT JOIN Category c ON p.categoryId = c.id;

-- Multiple joins with projection
selectDetailed {
  id: TEXT AS PaymentId,
  amount: INTEGER,
  userName: TEXT,
  categoryName: TEXT?
}:
SELECT
  p.id,
  p.amount,
  u.name AS userName,
  c.name AS categoryName
FROM Payment p
INNER JOIN User u ON p.userId = u.id
LEFT JOIN Category c ON p.categoryId = c.id
WHERE p.id = :paymentId;
```

## Aggregate Queries

```sql
-- Sum
totalByUser:
SELECT SUM(amount) FROM Payment WHERE userId = :userId;

-- Group by with count
countByStatus:
SELECT status, COUNT(*) AS count FROM Payment GROUP BY status;

-- Max/Min
latestPayment:
SELECT MAX(createdAt) FROM Payment WHERE userId = :userId;

-- Subquery for aggregation
selectWithTotalCount:
SELECT
  *,
  (SELECT COUNT(*) FROM Payment WHERE userId = p.userId) AS totalCount
FROM Payment p
WHERE p.id = :paymentId;
```

## Update Queries

```sql
-- Update single field
updateStatus:
UPDATE Payment SET status = :status WHERE id = :whereId;

-- Update multiple fields
updatePayment:
UPDATE Payment
SET
  amount = :amount,
  description = :description,
  updatedAt = :updatedAt
WHERE id = :whereId;

-- Conditional update
updateIfPending:
UPDATE Payment
SET status = :newStatus
WHERE id = :whereId AND status = 'pending';
```

## Usage in Kotlin

```kotlin
// Flow observation
override val paymentsFlow: Flow<List<Payment>>
  get() = db.paymentQueries
    .selectAll { id, userId, amount, description, createdAt ->
      Payment(id, userId, amount, description, createdAt)
    }
    .asFlow()
    .mapToList()

// Single read
override suspend fun getPayment(id: PaymentId) =
  db.paymentQueries
    .selectById(whereId = id)
    .awaitAsOneOrNull()
    ?.let { Payment(it.id, it.userId, it.amount, it.description, it.createdAt) }

// Write with transaction
override suspend fun upsertPayments(payments: List<Payment>) {
  db.transaction {
    payments.forEach { payment ->
      db.paymentQueries.upsert(
        id = payment.id,
        userId = payment.userId,
        amount = payment.amount,
        description = payment.description,
        createdAt = payment.createdAt,
      )
    }
  }
}
```
