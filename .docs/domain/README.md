# Domain Layer

The domain layer contains cross-module business logic and use cases.

## When to Use Domain Layer

Extract logic to domain layer when:
- Shared by multiple screens or features
- Complex business rules need enforcement
- Data aggregation from multiple sources needed
- Application-level orchestration required

**Don't create domain layer for**:
- Logic used by only one screen (keep in screen's Model)
- Simple data transformations
- UI-specific logic

## Module Structure

```
domain/<feature>/
+-- public/     # Use case interfaces and models
+-- impl/       # Real implementations
+-- fake/       # Test doubles
```

## Use Case Pattern

```kotlin
// public/
interface GetUserDetailsUseCase {
  suspend operator fun invoke(userId: String): UserDetails
}

// impl/
@Inject
@ContributesBinding(AppScope::class)
class RealGetUserDetailsUseCase(
  private val userRepository: UserRepository,
) : GetUserDetailsUseCase {
  override suspend fun invoke(userId: String): UserDetails {
    val user = userRepository.getUser(userId)
    return UserDetails(user)
  }
}
```

## VICE Source Pattern

For state-producing domain models, use VICE sources:

```kotlin
// public/
interface UserProfileModel : ViceSource<ViceLoadable<UserProfile>>

// impl/
@Inject
@ContributesBinding(AppScope::class)
class RealUserProfileModel(
  userRepository: UserRepository,
) : UserProfileModel, LoadableFlowSource<UserProfile>() {
  override val placeholder = UserProfile.Empty
  override val dataFlow = userRepository.observeCurrentUser()
}
```

## Layer Responsibilities

| Layer  | Responsibility                 |
|--------|--------------------------------|
| UI     | Display state, capture intents |
| Domain | Business logic, orchestration  |
| Data   | Data access, caching           |

Domain coordinates between Data layer and UI layer, transforming data for UI consumption.

## Related Documentation

- [../architecture/layers.md](/.docs/architecture/layers.md) - Complete layer architecture
