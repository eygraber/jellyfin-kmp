package template.ui.material.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import template.ui.material.R

@Composable
fun templateLightColorScheme(): ColorScheme =
  lightColorScheme(
    primary = colorResource(R.color.lightPrimary),
    onPrimary = colorResource(R.color.lightOnPrimary),
    primaryContainer = colorResource(R.color.lightPrimaryContainer),
    onPrimaryContainer = colorResource(R.color.lightOnPrimaryContainer),
    secondary = colorResource(R.color.lightSecondary),
    onSecondary = colorResource(R.color.lightOnSecondary),
    secondaryContainer = colorResource(R.color.lightSecondaryContainer),
    onSecondaryContainer = colorResource(R.color.lightOnSecondaryContainer),
    tertiary = colorResource(R.color.lightTertiary),
    onTertiary = colorResource(R.color.lightOnTertiary),
    tertiaryContainer = colorResource(R.color.lightTertiaryContainer),
    onTertiaryContainer = colorResource(R.color.lightOnTertiaryContainer),
    error = colorResource(R.color.lightError),
    onError = colorResource(R.color.lightOnError),
    errorContainer = colorResource(R.color.lightErrorContainer),
    onErrorContainer = colorResource(R.color.lightOnErrorContainer),
    background = colorResource(R.color.lightBackground),
    onBackground = colorResource(R.color.lightOnBackground),
    surface = colorResource(R.color.lightSurface),
    surfaceVariant = colorResource(R.color.lightSurfaceVariant),
    surfaceContainerLowest = colorResource(R.color.lightSurfaceContainerLowest),
    surfaceContainerLow = colorResource(R.color.lightSurfaceContainerLow),
    surfaceContainer = colorResource(R.color.lightSurfaceContainer),
    surfaceContainerHigh = colorResource(R.color.lightSurfaceContainerHigh),
    surfaceContainerHighest = colorResource(R.color.lightSurfaceContainerHighest),
    surfaceBright = colorResource(R.color.lightSurfaceBright),
    surfaceDim = colorResource(R.color.lightSurfaceDim),
    onSurface = colorResource(R.color.lightOnSurface),
    onSurfaceVariant = colorResource(R.color.lightOnSurfaceVariant),
    inverseSurface = colorResource(R.color.lightInverseSurface),
    inverseOnSurface = colorResource(R.color.lightInverseOnSurface),
    inversePrimary = colorResource(R.color.lightInversePrimary),
  )
