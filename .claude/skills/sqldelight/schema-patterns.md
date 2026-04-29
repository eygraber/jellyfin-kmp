# Schema Patterns

## Basic Table Definition

```sql
import com.eygraber.jellyfin.entity.common.ids.PaymentId;
import com.eygraber.jellyfin.entity.common.ids.UserId;

CREATE TABLE Payment(
  id TEXT AS PaymentId PRIMARY KEY NOT NULL,
  userId TEXT AS UserId NOT NULL REFERENCES User(id) ON DELETE CASCADE,
  amount INTEGER NOT NULL,
  description TEXT,
  createdAt INTEGER AS Instant NOT NULL
);

CREATE INDEX IX_Payment_userId ON Payment(userId);
```

## Custom Type Adapters

Types are mapped via column adapters in `ColumnAdapters.kt`:

```kotlin
// ID types (TEXT storage)
val PaymentIdAdapter = object : ColumnAdapter<PaymentId, String> {
  override fun decode(databaseValue: String) = PaymentId(databaseValue)
  override fun encode(value: PaymentId) = value.raw
}

// Enum types
val PaymentStatusAdapter = object : ColumnAdapter<PaymentStatus, String> {
  override fun decode(databaseValue: String) = PaymentStatus.valueOf(databaseValue)
  override fun encode(value: PaymentStatus) = value.name
}

// Instant (epoch millis)
val InstantAdapter = object : ColumnAdapter<Instant, Long> {
  override fun decode(databaseValue: Long) = Instant.fromEpochMilliseconds(databaseValue)
  override fun encode(value: Instant) = value.toEpochMilliseconds()
}

// Boolean
val BooleanAdapter = object : ColumnAdapter<Boolean, Long> {
  override fun decode(databaseValue: Long) = databaseValue == 1L
  override fun encode(value: Boolean) = if (value) 1L else 0L
}
```

Register adapters in `SqlDelight.kt`:

```kotlin
JellyfinDb(
  driver = driver,
  PaymentAdapter = Payment.Adapter(
    idAdapter = PaymentIdAdapter,
    userIdAdapter = UserIdAdapter,
    createdAtAdapter = InstantAdapter,
  ),
)
```

## Common Column Types

| Kotlin Type  | SQL Type             | Adapter        |
|--------------|----------------------|----------------|
| `String`     | `TEXT`               | None needed    |
| `Int`/`Long` | `INTEGER`            | None needed    |
| `Boolean`    | `INTEGER AS Boolean` | BooleanAdapter |
| `Instant`    | `INTEGER AS Instant` | InstantAdapter |
| Custom ID    | `TEXT AS MyId`       | Custom adapter |
| Enum         | `TEXT AS MyEnum`     | Custom adapter |

## Foreign Keys and Constraints

```sql
-- Foreign key with cascade delete
userId TEXT AS UserId NOT NULL REFERENCES User(id) ON DELETE CASCADE,

-- Foreign key with set null
categoryId TEXT AS CategoryId REFERENCES Category(id) ON DELETE SET NULL,

-- Composite primary key
PRIMARY KEY (id, attachmentId)

-- Unique constraint
UNIQUE (userId, name)
```

## Index Patterns

```sql
-- Single column index
CREATE INDEX IX_Payment_userId ON Payment(userId);

-- Composite index (order matters for queries)
CREATE INDEX IX_Message_caseId_sentAt ON Message(caseId, sentAt DESC);

-- Unique index
CREATE UNIQUE INDEX UX_User_email ON User(email);

-- Conditional index (partial index)
CREATE UNIQUE INDEX UX_Message_id_null ON Message(id) WHERE attachmentId IS NULL;
```

## Generated Columns

```sql
-- Computed column (SQLite 3.31+)
fullName TEXT GENERATED ALWAYS AS (firstName || ' ' || lastName) STORED;

-- Constant value column
staffType TEXT AS StaffType GENERATED ALWAYS AS ('attorney') STORED;
```

## Complete Example

```sql
import com.eygraber.jellyfin.entity.common.ids.TransactionId;
import com.eygraber.jellyfin.entity.common.ids.UserId;
import kotlinx.datetime.Instant;

CREATE TABLE Transaction(
  id TEXT AS TransactionId PRIMARY KEY NOT NULL,
  userId TEXT AS UserId NOT NULL REFERENCES User(id) ON DELETE CASCADE,
  amount INTEGER NOT NULL,
  currency TEXT NOT NULL DEFAULT 'USD',
  status TEXT AS TransactionStatus NOT NULL,
  description TEXT,
  createdAt INTEGER AS Instant NOT NULL,
  updatedAt INTEGER AS Instant
);

CREATE INDEX IX_Transaction_userId ON Transaction(userId);
CREATE INDEX IX_Transaction_status ON Transaction(status);
CREATE INDEX IX_Transaction_createdAt ON Transaction(createdAt DESC);
```
