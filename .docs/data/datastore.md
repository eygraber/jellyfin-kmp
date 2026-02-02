# DataStore

Preferences and typed data storage.

## Usage Pattern

DataStore with custom CBOR serializers:

```kotlin
val Context.authInfoDataStore: DataStore<AuthInfo> by dataStore(
  fileName = "auth_info",
  serializer = AuthInfoSerializer,
  corruptionHandler = ReplaceFileCorruptionHandler {
    AuthInfoSerializer.defaultValue
  },
)
```

## ABI Compatibility

If the data type ABI changes, serializer may crash reading old data.

### Safe Changes

- Add properties **with default values**

```kotlin
// Original
data class AuthInfo(val token: String)

// Safe addition
data class AuthInfo(
  val token: String,
  val refreshToken: String = "", // Has default
)
```

### Unsafe Changes

- Removing properties
- Changing property types
- Adding required properties

### Handling ABI Changes

**Option 1: Ignore Unknown Keys** (preserves data)
```kotlin
val cbor = Cbor { ignoreUnknownKeys = true }
```

**Option 2: Corruption Handler** (loses old data)
```kotlin
val dataStore by dataStore(
  fileName = "my_data",
  serializer = MySerializer,
  corruptionHandler = ReplaceFileCorruptionHandler {
    MySerializer.defaultValue
  },
)
```

The corruption handler catches exceptions from `readFrom` and replaces with default value.

**Choose based on data criticality**:
- Critical data -> Option 1 (never lose data)
- Recoverable data -> Option 2 (simpler migration)

## Serializer Example

```kotlin
object AuthInfoSerializer : Serializer<AuthInfo> {
  override val defaultValue = AuthInfo(token = "")

  override suspend fun readFrom(input: InputStream): AuthInfo {
    return try {
      Cbor.decodeFromByteArray(input.readBytes())
    } catch (e: Exception) {
      throw CorruptionException("Cannot read AuthInfo", e)
    }
  }

  override suspend fun writeTo(t: AuthInfo, output: OutputStream) {
    output.write(Cbor.encodeToByteArray(t))
  }
}
```
