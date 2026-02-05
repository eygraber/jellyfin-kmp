# Navigation

Uses [AndroidX Navigation3](https://developer.android.com/guide/navigation/navigation-3) with `vice-nav3`.

## Structure

The `nav` module contains `JellyfinNav` composable with a `NavDisplay` as the navigation root.

## NavEntry Pattern

Each screen provides a `ViceNavEntryProvider`:

```kotlin
@Inject
@SingleIn(ScreenScope::class)
class MyScreenNavEntryProvider(
  override val compositor: MyScreenCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> MyScreenView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

private typealias Key = MyScreenKey
private typealias View = MyScreenView
private typealias Intent = MyScreenIntent
private typealias Compositor = MyScreenCompositor
private typealias Effects = ViceEffects
private typealias ViewState = MyScreenViewState
```

## Navigation Keys

Define keys as data classes/objects:

```kotlin
@Serializable
data class DetailsKey(val id: String) : NavKey

@Serializable
data object HomeKey : NavKey
```

## Navigation Events

`NavBackStack` is NOT exposed outside `nav`. Instead, screens receive navigation as lambdas:

```kotlin
class MyScreenNavigator(
  private val onBack: () -> Unit,
  private val onNavigateToDetails: (id: String) -> Unit,
) {
  fun navigateBack() {
    onBack()
  }

  fun navigateToDetails(id: String) {
    onNavigateToDetails(id)
  }
}
```

This [encapsulates navigation](https://developer.android.com/guide/navigation/design/encapsulate) and prevents leaking library implementation details into screens.

## Navigating

From `JellyfinNav`, navigation happens centrally:

```kotlin
val navigator = MyScreenNavigator(
  onBack = { navBackStack.pop() },
  onNavigateToDetails = { id ->
    navBackStack.push(DetailsKey(id))
  },
)
```

## DI Integration

Each `ViceNavEntryProvider` has an associated `ScreenGraph`:

```kotlin
@GraphExtension(ScreenScope::class)
interface MyScreenGraph {
  val navEntryProvider: MyScreenNavEntry

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createMyScreenGraph(
      @Provides navigator: MyScreenNavigator,
      @Provides key: MyScreenKey,
    ): MyScreenGraph
  }
}
```

See [DI](/.docs/di/README.md) for scope details.
