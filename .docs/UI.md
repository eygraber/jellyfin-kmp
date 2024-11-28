# Template Android UI Layer

> The role of the UI is to display the application data on the screen and also to
> serve as the primary point of user interaction <sup>[1]</sup>

## Module Generation

Run `.scripts/generate_module` to generate the initial state of a `destination` module for a given feature.

## Destinations

`ViceDestination` is the entry point for each screen into the [VICE] framework.

Each `ViceDestination` is encapsulated in its own module nested under the `destinations` directory.

There should not be more than one top level `ViceDestination` per module.
If the screen is broken into multiple children the child `ViceDestination` can live in the same module as the parent.

The [DI DestinationComponent] creates a subgraph in the app's [dependency graph]:

It `@Provides` the route used to navigate to it, as well as a `Navigator` class that contains the
"navigation events" for the destination (see [Navigation](#navigation) below).

## Navigation

[AndroidX Navigation Compose] is used together with `vice-nav` to provide navigation between `ViceDestinations`.

The `nav` module contains the top level `NavGraphBuilder` which it provides to the `NavHost` in the `app` module.

It will add `ViceDestinations` to the nav graph by creating an instance of the destination's component, which
exposes a `ViceDestination` instance.

The `NavController` is not exposed outside of `nav`. Instead, `ViceDestination` receives "navigation events"
in the form of lambdas. This allows all navigation to happen centrally in the `NavGraphBuilder`, and doesn't leak
navigation library implementation details into the destinations. See [Encapsulate your navigation code].

[1]: https://developer.android.com/topic/architecture/ui-layer

[AndroidX Navigation Compose]: https://developer.android.com/develop/ui/compose/navigation
[DI DestinationComponent]: ./DI.md#template-di
[dependency graph]: ./DI.md
[Encapsulate your navigation code]: https://developer.android.com/guide/navigation/design/encapsulate
[VICE]: https://github.com/eygraber/vice
