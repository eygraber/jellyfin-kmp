---
name: ktorfit
description: Work with Ktorfit API clients - create endpoints, handle responses, debug network issues.
argument-hint: "[task] - e.g., 'create user endpoint', 'debug auth issue', 'explain response handling'"
context: fork
allowed-tools: Read, Edit, Write, Bash(./format, ./gradlew *, grep, cat, mkdir), Glob, Grep
---

# Ktorfit Skill

Work with Ktorfit API clients - create endpoints, handle responses, debug network issues.

## Common Tasks

```
/ktorfit create user endpoint          # Create new API endpoint
/ktorfit debug auth issue              # Debug authentication problems
/ktorfit explain response handling     # Understand response patterns
/ktorfit add retry logic               # Add retry to endpoint
```

## API Interface Pattern

```kotlin
internal interface UserApi {
  @GET("users/{id}")
  suspend fun getUser(
    @Path("id") userId: String,
  ): JellyfinResponse<JsonObject>

  @POST("users")
  suspend fun createUser(
    @Body request: CreateUserRequest,
  ): JellyfinResponse<JsonObject>

  @GET("users")
  suspend fun getUsers(
    @Query("page") page: Int,
    @Query("limit") limit: Int = 20,
  ): JellyfinResponse<JsonArray>
}
```

## Response Handling

```kotlin
// Convert to result
val result = api.getUser(userId).toResult()

// Map success
result.mapSuccessTo { json ->
  User(
    id = json["id"]!!.jsonPrimitive.content,
    name = json["name"]!!.jsonPrimitive.content,
  )
}

// Handle in repository
override suspend fun fetchUser(id: String): JellyfinResult<User> {
  return api.getUser(id)
    .toResult()
    .mapSuccessTo { json -> json.toUser() }
    .andThen { user ->
      localDataSource.saveUser(user)
    }
}
```

## Key Patterns

### Request Body (kotlinx.serialization)
```kotlin
@Serializable
data class CreateUserRequest(
  val name: String,
  val email: String,
)
```

### Headers
```kotlin
@GET("users")
suspend fun getUsers(
  @Header("Authorization") token: String,
): JellyfinResponse<JsonArray>
```

### Retry Logic
```kotlin
retryJellyfinResult(RetryPolicy.Default) {
  api.getUsers().toResult()
}
```

## Module Location

API interfaces are in `data/{feature}/impl/` and are `internal`.

## Documentation

- [.docs/data/ktorfit.md](/.docs/data/ktorfit.md) - Complete patterns
