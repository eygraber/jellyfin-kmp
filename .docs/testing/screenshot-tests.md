# Screenshot Tests

Prevent UI regressions with visual comparison.

## Framework

[Paparazzi](https://github.com/cashapp/paparazzi) for screenshot testing.

## Test Modes

The project has two distinct modes of screenshot testing:

1. **Screen Tests** (`:screens` modules) - Test complete screen UIs with device configurations
2. **Component Tests** (`:ui` modules) - Test individual UI components with focused state variations

### When to Use Each Mode

- **Screen Tests**: For full screen views that need testing across multiple device configs (light/dark, different sizes)
- **Component Tests**: For reusable UI components testing specific states, sizes, and edge cases

## Screen Test

```kotlin
import app.cash.paparazzi.Paparazzi
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class MyScreenScreenshotTest(
  @param:TestParameter
  private val deviceConfig: PaparazziDeviceConfig,
) {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = deviceConfig,
  )

  @Test
  fun screenshot() {
    ViewStatePreviewProvider()
      .values
      .forEach { (name, state) ->
        paparazzi.snapshot(name = name) {
          JellyfinEdgeToEdgePreviewTheme(isDarkMode = deviceConfig.isDarkMode) {
            MyScreenView(
              state = state,
              onIntent = {},
            )
          }
        }
      }
  }
}
```

## Component Test

For individual UI components in `:ui` modules:

```kotlin
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

class MyComponentScreenshotTest {
  @get:Rule
  val paparazzi = Paparazzi()

  @Test
  fun loading() {
    paparazzi.snapshot {
      JellyfinPreviewTheme {
        MyComponent(
          isLoading = true,
          // ... component props
        )
      }
    }
  }

  @Test
  fun success() {
    paparazzi.snapshot {
      JellyfinPreviewTheme {
        MyComponent(
          isLoading = false,
          // ... component props
        )
      }
    }
  }

  // Test multiple variations in a loop
  @Test
  fun sizes() {
    listOf(24.dp, 48.dp, 96.dp).forEach { size ->
      paparazzi.snapshot(name = "${size}") {
        JellyfinPreviewTheme {
          MyComponent(
            modifier = Modifier.size(size),
          )
        }
      }
    }
  }
}
```

**Key differences from Screen tests:**
- No `PaparazziDeviceConfig` parameter (components don't need device configs)
- Uses `JellyfinPreviewTheme` instead of `JellyfinEdgeToEdgePreviewTheme`
- Multiple `@Test` methods for different component states
- Can use `@TestParameter` for parameterized variations
- Focus on testing component edge cases and states

## Commands

```bash
# Generate golden images
./gradlew :module:recordPaparazziDebug

# Verify against golden images
./gradlew :module:verifyPaparazziDebug

# Clean golden images
./gradlew :module:cleanPaparazziDebug

# Clean and regenerate
./gradlew :module:cleanRecordPaparazziDebug
```

## Robolectric Tests

When writing tests that require Robolectric (Android framework dependencies), extend the appropriate base class:

```kotlin
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class MyFeatureTest : BaseRobolectricTest() {
  @Test
  fun `test with android context`() {
    // Test implementation
  }
}
```
