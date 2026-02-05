# Screenshot Test Patterns

## Simple Component Tests

For straightforward components with a few states:

```kotlin
class UserAvatarScreenshotTest {
  @get:Rule
  val paparazzi = Paparazzi()

  @Test
  fun loading() {
    paparazzi.snapshot {
      JellyfinPreviewTheme {
        UserAvatar(
          isLoading = true,
          url = JellyfinPreviewImages.AvatarMichael,
          name = "Michael Scott",
          palette = BrandPalette.Jellyfin,
          modifier = Modifier.size(48.dp),
        )
      }
    }
  }

  @Test
  fun successWithImage() {
    paparazzi.snapshot {
      JellyfinPreviewAsyncImageProvider {
        JellyfinPreviewTheme {
          UserAvatar(
            isLoading = false,
            url = JellyfinPreviewImages.AvatarMichael,
            name = "Michael Scott",
            palette = BrandPalette.Jellyfin,
            modifier = Modifier.size(48.dp),
          )
        }
      }
    }
  }

  @Test
  fun noUrl_showsInitial() {
    paparazzi.snapshot {
      JellyfinPreviewTheme {
        UserAvatar(
          isLoading = false,
          url = null,
          name = "Jim Halpert",
          palette = BrandPalette.Jellyfin,
          modifier = Modifier.size(48.dp),
        )
      }
    }
  }
}
```

## Components with Preview Providers

For components with many variations, reuse preview providers:

```kotlin
@RunWith(TestParameterInjector::class)
class JellyfinCardScreenshotTest {
  @get:Rule
  val paparazzi = Paparazzi()

  @Test
  fun screenshot() {
    JellyfinCardPreviewParamProvider()
      .values
      .forEach { param ->
        paparazzi.snapshot(name = param.name) {
          JellyfinCardPreview(param)
        }
      }
  }
}
```

## Components with Device Configs

Test across different device configurations (dark mode, sizes):

```kotlin
@RunWith(TestParameterInjector::class)
class CaseNavBarScreenshotTest(
  @param:TestParameter
  private val deviceConfig: PaparazziDeviceConfig,
) {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = deviceConfig.config,
  )

  @Test
  fun screenshot() {
    CaseNavBarPreviewParamProvider()
      .values
      .forEach { param ->
        paparazzi.snapshot(name = param.name) {
          CaseNavAppBarPreview(param)
        }
      }
  }
}
```

## Parameterized Tests with TestParameterInjector

For testing enum values or predefined sets:

```kotlin
@RunWith(TestParameterInjector::class)
class SemanticIconScreenshotTest {
  @get:Rule
  val paparazzi = Paparazzi()

  @Test
  fun screenshotSemanticColors(
    @TestParameter
    colors: SemanticColorsValues,
  ) {
    paparazzi.snapshot {
      JellyfinPreviewTheme {
        Surface {
          Box(modifier = Modifier.padding(16.dp)) {
            SemanticIcon(
              imageVector = JellyfinIcons.Close,
              contentDescription = null,
              colors = colors.value,
              modifier = Modifier.size(48.dp),
            )
          }
        }
      }
    }
  }
}

enum class SemanticColorsValues(val value: SemanticColors) {
  Red(SemanticColors.Red),
  Yellow(SemanticColors.Yellow),
  Green(SemanticColors.Green),
  Neutral(SemanticColors.Neutral),
}
```

## Screen Tests with ViewStatePreviewProvider

Full screens with device configs and view state providers:

```kotlin
@RunWith(TestParameterInjector::class)
class CaseListScreenshotTest(
  @param:TestParameter
  private val deviceConfig: PaparazziDeviceConfig,
) {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = deviceConfig.config,
  )

  @Test
  fun screenshot() {
    ViewStatePreviewProvider()
      .values
      .forEach { (name, state) ->
        paparazzi.snapshot(name = name) {
          JellyfinEdgeToEdgePreviewTheme(isDarkMode = deviceConfig.isDarkMode) {
            JellyfinPreviewAsyncImageProvider {
              CaseListView(
                state = state,
                onIntent = {},
              )
            }
          }
        }
      }
  }
}
```

## ViewStatePreviewProvider Pattern

Define named view states in the screen's module:

```kotlin
internal class CaseListViewStatePreviewProvider : NamedPreviewParameterProvider<CaseListViewState>() {
  private val loadingState = CaseListViewState(
    palette = BrandPalette.Jellyfin,
    cases = ViceLoadable.Loading(),
    // ... other properties
  )

  private val successState = CaseListViewState(
    palette = BrandPalette.Jellyfin,
    cases = ViceLoadable.Loaded(/* data */),
    // ... other properties
  )

  override val values = sequenceOf(
    "Loading" to loadingState,
    "Success" to successState,
    // ... more states
  )
}
```

## Image Loading with Coil

Always wrap with `JellyfinPreviewAsyncImageProvider` when using Coil:

```kotlin
@Test
fun successWithImage() {
  paparazzi.snapshot {
    JellyfinPreviewAsyncImageProvider {
      JellyfinPreviewTheme {
        MyComponent(
          imageUrl = JellyfinPreviewImages.AvatarMichael,
        )
      }
    }
  }
}
```

## Pattern Summary

| Type              | Theme                            | Device Config | Use Case           |
|-------------------|----------------------------------|---------------|--------------------|
| Simple component  | `JellyfinPreviewTheme`           | No            | Few states         |
| Complex component | `JellyfinPreviewTheme`           | Optional      | Many variations    |
| Screen            | `JellyfinEdgeToEdgePreviewTheme` | Yes           | Full screen views  |
| Parameterized     | Either                           | Optional      | Enum/value testing |
