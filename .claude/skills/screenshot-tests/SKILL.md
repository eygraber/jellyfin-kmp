---
name: screenshot-tests
description: Write Paparazzi screenshot tests for Compose components.
argument-hint: "[component] - e.g., 'UserAvatar', 'screens/welcome'"
context: fork
allowed-tools: Read, Edit, Write, Bash(./gradlew *, grep), Glob, Grep
---

# Screenshot Tests Skill

Write Paparazzi screenshot tests for Compose components and screens.

## Common Tasks

```
/screenshot-tests UserAvatar        # Test a UI component
/screenshot-tests screens/Welcome   # Test a screen
/screenshot-tests verify ui/common  # Verify existing tests
/screenshot-tests record MyCard     # Record new golden images
```

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

## Test Location

```
ui/<feature>/src/test/kotlin/com/template/ui/<feature>/components/
    +-- MyComponentScreenshotTest.kt

screens/<feature>/src/test/kotlin/com/template/screens/<feature>/
    +-- MyScreenScreenshotTest.kt
```

## Quick Patterns

### Component Test (simple)

```kotlin
class UserAvatarScreenshotTest {
  @get:Rule
  val paparazzi = Paparazzi()

  @Test
  fun loading() {
    paparazzi.snapshot {
      TemplatePreviewTheme {
        UserAvatar(isLoading = true, ...)
      }
    }
  }

  @Test
  fun success() {
    paparazzi.snapshot {
      TemplatePreviewTheme {
        UserAvatar(isLoading = false, ...)
      }
    }
  }
}
```

### Screen Test (with device configs)

```kotlin
@RunWith(TestParameterInjector::class)
class MyScreenScreenshotTest(
  @param:TestParameter
  private val deviceConfig: PaparazziDeviceConfig,
) {
  @get:Rule
  val paparazzi = Paparazzi(deviceConfig = deviceConfig.config)

  @Test
  fun screenshot() {
    ViewStatePreviewProvider()
      .values
      .forEach { (name, state) ->
        paparazzi.snapshot(name = name) {
          TemplateEdgeToEdgePreviewTheme(isDarkMode = deviceConfig.isDarkMode) {
            MyScreenView(state = state, onIntent = {})
          }
        }
      }
  }
}
```

## Key Rules

- **Components**: Use `TemplatePreviewTheme`, no device configs
- **Screens**: Use `TemplateEdgeToEdgePreviewTheme` with `PaparazziDeviceConfig`
- **Images**: Wrap with `TemplatePreviewAsyncImageProvider` if using Coil

## Additional Resources

- [patterns.md](patterns.md) - Detailed test patterns
- [.docs/testing/screenshot-tests.md](/.docs/testing/screenshot-tests.md) - Complete guide
