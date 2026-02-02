# Flow Tests

Test reactive data streams with Turbine.

## Basic Flow Test

```kotlin
import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DataSourceTest {
  @Test
  fun `when data updated, flow emits`() = runTest {
    val dataSource = MyDataSource()

    dataSource.dataFlow.test {
      awaitItem() shouldBe null // Initial

      dataSource.updateData("new value")

      awaitItem() shouldBe "new value"

      cancelAndIgnoreRemainingEvents()
    }
  }
}
```

## Turbine Methods

| Method                             | Purpose               |
|------------------------------------|-----------------------|
| `awaitItem()`                      | Get next emission     |
| `awaitComplete()`                  | Verify flow completed |
| `awaitError()`                     | Verify flow errored   |
| `skipItems(n)`                     | Skip n emissions      |
| `cancelAndIgnoreRemainingEvents()` | Clean up              |

## TestSubjectCoordinator

For complex async operations with precise timing:

```kotlin
@Test
fun `state transitions correctly`() = withTestSubjectCoordinator {
  val resultChannel = Channel<TemplateResult<String>>()

  runTest(
    driver = { model ->
      wait() // Wait for validation's first check
      model.triggerOperation()
      wait() // Wait for loading check
      resultChannel.send(TemplateResult.Success("data"))
      wait() // Wait for success check
    },
    validation = {
      awaitItem() shouldBe State.Initial
      proceed() // Let driver continue

      awaitItem().isLoading shouldBe true
      proceed() // Let driver send result

      awaitItem().data shouldBe "data"
      proceed() // Let driver finish
    },
    resultChannel = resultChannel,
  )
}
```

### How It Works

1. `validation` block asserts state
2. Calls `proceed()` to unblock driver
3. `driver` performs action, calls `wait()`
4. Repeats until test complete

This creates "turn-based" execution for deterministic async tests.

## Common Patterns

### Testing Emissions

```kotlin
dataFlow.test {
  val items = mutableListOf<Data>()
  repeat(3) { items.add(awaitItem()) }

  items shouldHaveSize 3
}
```

### Testing No More Emissions

```kotlin
dataFlow.test {
  awaitItem() shouldBe expected
  expectNoEvents() // Verify no more emissions
}
```
