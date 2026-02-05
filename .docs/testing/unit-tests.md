# Unit Tests

Test individual components in isolation.

## Basic Structure

```kotlin
import io.kotest.matchers.shouldBe
import org.junit.Test

class MyClassTest {
  @Test
  fun `given state, when action, then result`() {
    // Arrange
    val subject = MyClass()

    // Act
    val result = subject.performAction()

    // Assert
    result shouldBe expected
  }
}
```

## Fakes Over Mocks

**Always prefer fakes**. MockK only when impossible to fake.

```kotlin
// Good - fake
val repo = FakeUserRepository()

// Bad - mock (avoid unless necessary)
val repo = mockk<UserRepository>()
```

Create fakes in `fake/` modules or test source sets:

```kotlin
class FakeUserRepository : UserRepository {
  var dataToReturn: User? = null
  var shouldFail = false
  var callCount = 0

  override suspend fun getUser(): JellyfinResult<User> {
    callCount++
    return when {
      shouldFail -> JellyfinResult.Failure(Exception("Test"))
      dataToReturn != null -> JellyfinResult.Success(dataToReturn!!)
      else -> JellyfinResult.Failure(Exception("No data"))
    }
  }
}
```

## Assertions

Use Kotest:

```kotlin
// Good
result shouldBe expectedValue
result shouldNotBe null
list shouldHaveSize 3
string shouldContain "expected"

// Bad - JUnit assertions
assertEquals(expected, result)
assertNotNull(result)
```

## Test Naming

Use backticks for readable names:

- `when user clicks submit, form validates`
- `given invalid input, returns error`
- `loading state shows while fetching`

Avoid: `testSubmit`, `test1`
