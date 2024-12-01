# Android Arch @ Template

Template's Android architecture is inspired by the [Android App Architecture],
consisting of a [UI layer], [Domain layer], and [Data layer].

## Principles

  - Immutability
  - Well defined boundaries

## Android Glue

These live in the `app` module.

### Application

`TemplateApplication` is a custom `Application` that holds the [DI AppComponent] instance,
and any needed system hooks. Any initialization needed should be delegated to `TemplateInitializer`.

### Activity

`TemplateActivity` is the sole `Activity` in the project which exists to:

> represent the contract between the Android OS and your app...To provide a satisfactory user experience
> and a more manageable app maintenance experience, it's best to minimize your dependency on them.<sup>[1]</sup>

It also functions as the entrypoint into Compose UI, and holds the [DI ActivityComponent] instance.

## Template

### Destinations

Screens in the app are built using [VICE], and define a `TemplateDestination` along with all of the required
VICE components (`ViceView`, `Intent`, `ViceCompositor`, `ViceEffects`, and `ViewState`).

Each `TemplateDestination` defines and holds a [DI DestinationComponent].

They live in modules nested under the `ui` directory.

See [UI Destinations] for more details.

### Services

Lives in the `services` module.

#### Template Initializer

[1]: https://developer.android.com/topic/architecture#separation-of-concerns

[Android App Architecture]: https://developer.android.com/topic/architecture
[Data layer]: https://developer.android.com/topic/architecture/data-layer
[Domain layer]: https://developer.android.com/topic/architecture/domain-layer
[UI layer]: https://developer.android.com/topic/architecture/ui-layer

[DI ActivityComponent]: ./DI.md#architectural-components
[DI AppComponent]: ./DI.md#architectural-components
[DI DestinationComponent]: ./DI.md#destination-components

[UI Destinations]: ./UI.md#destinations

[VICE]: https://github.com/eygraber/vice
