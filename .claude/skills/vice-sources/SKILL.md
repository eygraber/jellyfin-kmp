---
name: vice-sources
description: Create ViceSource models for screen business logic and reactive state management.
argument-hint: "[task] - e.g., 'create UserModel', 'explain ViceSource pattern'"
context: fork
allowed-tools: Read, Edit, Write, Glob, Grep, Bash(./format, ./gradlew *)
---

# ViceSource Skill

Create ViceSource models for screen business logic and reactive state management.

## When to Use ViceSource

**Use ViceSource when:**
- Screen needs reactive business logic
- State transforms based on repository data
- Complex async operations in screen context

**Use Behavior Model (simple @Inject) when:**
- Pure validation/formatting
- No reactive state needed
- Stateless transformations

## ViceSource Pattern

```kotlin
@ScreenScope
class UserModel @Inject constructor(
  private val userRepository: UserRepository,
) : ViceSource<UserState> {

  @Composable
  override fun currentState(): UserState {
    val users by userRepository.observeUsers()
      .collectAsStateWithLifecycle(emptyList())

    return UserState(
      users = users,
      isLoading = users.isEmpty(),
    )
  }
}

data class UserState(
  val users: List<User>,
  val isLoading: Boolean,
)
```

## Integration with Compositor

```kotlin
@ScreenScope
class UserCompositor @Inject constructor(
  private val userModel: UserModel,
  private val navigator: Navigator,
) : ViceCompositor<UserIntent, UserViewState>() {

  @Composable
  override fun composite() = with(userModel.currentState()) {
    UserViewState(
      users = users.map { it.toUiModel() },
      isLoading = isLoading,
    )
  }

  override suspend fun onIntent(intent: UserIntent) {
    when (intent) {
      is UserIntent.UserClick -> navigator.navigateTo(UserDetails(intent.userId))
      is UserIntent.Refresh -> userRepository.refresh()
    }
  }
}
```

## Multiple Sources

```kotlin
@Composable
override fun composite(): MyViewState {
  val userState = userModel.currentState()
  val settingsState = settingsModel.currentState()

  return MyViewState(
    userName = userState.name,
    isDarkMode = settingsState.isDarkMode,
  )
}
```

## Testing ViceSource

Use `moleculeFlow` + Turbine:

```kotlin
@Test
fun `state updates when repository emits`() = runTest {
  val repository = FakeUserRepository()
  val model = UserModel(repository)

  moleculeFlow(RecompositionMode.Immediate) {
    model.currentState()
  }.test {
    awaitItem().isLoading shouldBe true

    repository.emit(listOf(testUser))

    awaitItem().users shouldBe listOf(testUser)
  }
}
```

## Documentation

- [.docs/architecture/vice-pattern.md](/.docs/architecture/vice-pattern.md) - VICE pattern
- [.claude/rules/examples/good-model-test.kt](/.claude/rules/examples/good-model-test.kt) - Test examples
