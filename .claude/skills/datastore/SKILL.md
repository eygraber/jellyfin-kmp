---
name: datastore
description: Work with DataStore for simple key-value persistence.
argument-hint: "[task] - e.g., 'create settings store', 'add preference', 'migrate from SharedPreferences'"
context: fork
allowed-tools: Read, Edit, Write, Bash(./format, ./gradlew *, grep, cat, mkdir), Glob, Grep
---

# DataStore Skill

Work with DataStore for simple key-value persistence.

## Common Tasks

```
/datastore create settings store        # Create new DataStore
/datastore add preference               # Add new preference key
/datastore explain patterns             # Understand DataStore patterns
```

## When to Use DataStore

**Use DataStore for:**
- User preferences and settings
- Simple key-value data
- Small amounts of data

**Use SQLDelight instead for:**
- Structured relational data
- Large amounts of data
- Complex queries

## DataStore Pattern

```kotlin
@SingleIn(AppScope::class)
class SettingsDataStore @Inject constructor(
  @param:AppContext private val context: Context,
) {
  private val Context.dataStore by preferencesDataStore(name = "settings")

  val isDarkModeEnabled: Flow<Boolean> = context.dataStore.data
    .map { preferences ->
      preferences[DARK_MODE_KEY] ?: false
    }

  suspend fun setDarkModeEnabled(enabled: Boolean) {
    context.dataStore.edit { preferences ->
      preferences[DARK_MODE_KEY] = enabled
    }
  }

  companion object {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
  }
}
```

## Key Patterns

### Preference Keys
```kotlin
val STRING_KEY = stringPreferencesKey("string_key")
val INT_KEY = intPreferencesKey("int_key")
val BOOL_KEY = booleanPreferencesKey("bool_key")
val LONG_KEY = longPreferencesKey("long_key")
val FLOAT_KEY = floatPreferencesKey("float_key")
```

### Reading with Default
```kotlin
val value: Flow<String> = dataStore.data
  .map { it[STRING_KEY] ?: "default" }
```

### Writing
```kotlin
suspend fun setValue(value: String) {
  dataStore.edit { it[STRING_KEY] = value }
}
```

### Clearing
```kotlin
suspend fun clear() {
  dataStore.edit { it.clear() }
}
```

## Documentation

- [.docs/data/datastore.md](/.docs/data/datastore.md) - Complete patterns
