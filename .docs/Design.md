# Template Android Design

## Icons

Any icon that is needed in the app should be added to the `icons` module as an `ImageVector` extension on `TemplateIcons`.

Usage of `androidx.compose.material.icons` APIs are prohibited in all modules except for `icons`.

[There is an IDE plugin] that can convert an SVG or SVG Drawable to a Compose `ImageVector`.

[There is an IDE plugin]: https://plugins.jetbrains.com/plugin/24786-valkyrie--svg-to-imagevector
