---
name: screenshot-tests
description: Generate Paparazzi screenshot tests for screens and UI components.
argument-hint: "[file] - e.g., 'MyScreen.kt', 'MyComponent.kt'"
context: fork
allowed-tools: Read, Edit, Write, Glob, Grep, Bash(./gradlew *)
---

# Screenshot Tests

Generate Paparazzi screenshot tests following project patterns.

## Usage

```
/screenshot-tests MyScreenView.kt       # Generate screen tests
/screenshot-tests MyComponent.kt        # Generate component tests
```

## Two Modes

### 1. Screen Tests (`:screens` modules)

Test full screens with multiple ViewState variations:

```kotlin
class MyScreenViewScreenshotTest {
  @get:Rule
  val paparazzi = paparazziScreenshotRule()

  @Test
  fun screenshots() {
    PaparazziDeviceConfig.entries.forEach { device ->
      MyViewStatePreviewProvider().values.forEach { state ->
        paparazzi.snapshotScreen(
          name = "device=${device.name}_state=${state.name}",
          deviceConfig = device,
        ) {
          SuperDoEdgeToEdgePreviewTheme {
            MyView(state = state, onIntent = {})
          }
        }
      }
    }
  }
}
```

### 2. Component Tests (`:ui` modules)

Test individual UI components with custom methods:

```kotlin
class MyComponentScreenshotTest {
  @get:Rule
  val paparazzi = paparazziScreenshotRule()

  @Test
  fun `default state`() {
    paparazzi.snapshotComponent {
      SuperDoPreviewTheme {
        MyComponent(text = "Hello")
      }
    }
  }

  @Test
  fun `loading state`() {
    paparazzi.snapshotComponent {
      SuperDoPreviewTheme {
        MyComponent(isLoading = true)
      }
    }
  }
}
```

## Key Patterns

- **Use ViewStatePreviewProvider** for screens with multiple states
- **Test on multiple devices** using `PaparazziDeviceConfig.entries`
- **Wrap content in proper theme** (`SuperDoEdgeToEdgePreviewTheme` for screens)
- **Wrap Coil images** in `SuperDoPreviewAsyncImageProvider` if needed

## Commands

```bash
./gradlew verifyPaparazziDebug    # Verify against golden images
./gradlew recordPaparazziDebug    # Record new golden images
```

## Process

1. **Read** the source file to understand component/screen structure
2. **Check** for existing PreviewProvider or preview functions
3. **Generate** test class following appropriate pattern
4. **Run** `./gradlew :module:verifyPaparazziDebug` to verify
5. **Record** if new: `./gradlew :module:recordPaparazziDebug`
