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
      TemplatePreviewTheme {
        UserAvatar(
          isLoading = true,
          url = TemplatePreviewImages.AvatarMichael,
          name = "Michael Scott",
          palette = BrandPalette.Template,
          modifier = Modifier.size(48.dp),
        )
      }
    }
  }

  @Test
  fun successWithImage() {
    paparazzi.snapshot {
      TemplatePreviewAsyncImageProvider {
        TemplatePreviewTheme {
          UserAvatar(
            isLoading = false,
            url = TemplatePreviewImages.AvatarMichael,
            name = "Michael Scott",
            palette = BrandPalette.Template,
            modifier = Modifier.size(48.dp),
          )
        }
      }
    }
  }

  @Test
  fun noUrl_showsInitial() {
    paparazzi.snapshot {
      TemplatePreviewTheme {
        UserAvatar(
          isLoading = false,
          url = null,
          name = "Jim Halpert",
          palette = BrandPalette.Template,
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
class TemplateCardScreenshotTest {
  @get:Rule
  val paparazzi = Paparazzi()

  @Test
  fun screenshot() {
    TemplateCardPreviewParamProvider()
      .values
      .forEach { param ->
        paparazzi.snapshot(name = param.name) {
          TemplateCardPreview(param)
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
      TemplatePreviewTheme {
        Surface {
          Box(modifier = Modifier.padding(16.dp)) {
            SemanticIcon(
              imageVector = TemplateIcons.Close,
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
          TemplateEdgeToEdgePreviewTheme(isDarkMode = deviceConfig.isDarkMode) {
            TemplatePreviewAsyncImageProvider {
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
    palette = BrandPalette.Template,
    cases = ViceLoadable.Loading(),
    // ... other properties
  )

  private val successState = CaseListViewState(
    palette = BrandPalette.Template,
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

Always wrap with `TemplatePreviewAsyncImageProvider` when using Coil:

```kotlin
@Test
fun successWithImage() {
  paparazzi.snapshot {
    TemplatePreviewAsyncImageProvider {
      TemplatePreviewTheme {
        MyComponent(
          imageUrl = TemplatePreviewImages.AvatarMichael,
        )
      }
    }
  }
}
```

## Pattern Summary

| Type              | Theme                            | Device Config | Use Case           |
|-------------------|----------------------------------|---------------|--------------------|
| Simple component  | `TemplatePreviewTheme`           | No            | Few states         |
| Complex component | `TemplatePreviewTheme`           | Optional      | Many variations    |
| Screen            | `TemplateEdgeToEdgePreviewTheme` | Yes           | Full screen views  |
| Parameterized     | Either                           | Optional      | Enum/value testing |
