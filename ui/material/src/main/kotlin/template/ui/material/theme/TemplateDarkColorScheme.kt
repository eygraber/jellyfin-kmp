package template.ui.material.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import template.ui.material.R

@Composable
fun templateDarkColorScheme(): ColorScheme =
  darkColorScheme(
    primary = colorResource(R.color.darkPrimary),
    onPrimary = colorResource(R.color.darkOnPrimary),
    primaryContainer = colorResource(R.color.darkPrimaryContainer),
    onPrimaryContainer = colorResource(R.color.darkOnPrimaryContainer),
    secondary = colorResource(R.color.darkSecondary),
    onSecondary = colorResource(R.color.darkOnSecondary),
    secondaryContainer = colorResource(R.color.darkSecondaryContainer),
    onSecondaryContainer = colorResource(R.color.darkOnSecondaryContainer),
    tertiary = colorResource(R.color.darkTertiary),
    onTertiary = colorResource(R.color.darkOnTertiary),
    tertiaryContainer = colorResource(R.color.darkTertiaryContainer),
    onTertiaryContainer = colorResource(R.color.darkOnTertiaryContainer),
    error = colorResource(R.color.darkError),
    onError = colorResource(R.color.darkOnError),
    errorContainer = colorResource(R.color.darkErrorContainer),
    onErrorContainer = colorResource(R.color.darkOnErrorContainer),
    background = colorResource(R.color.darkBackground),
    onBackground = colorResource(R.color.darkOnBackground),
    surface = colorResource(R.color.darkSurface),
    surfaceVariant = colorResource(R.color.darkSurfaceVariant),
    surfaceContainerLowest = colorResource(R.color.darkSurfaceContainerLowest),
    surfaceContainerLow = colorResource(R.color.darkSurfaceContainerLow),
    surfaceContainer = colorResource(R.color.darkSurfaceContainer),
    surfaceContainerHigh = colorResource(R.color.darkSurfaceContainerHigh),
    surfaceContainerHighest = colorResource(R.color.darkSurfaceContainerHighest),
    surfaceBright = colorResource(R.color.darkSurfaceBright),
    surfaceDim = colorResource(R.color.darkSurfaceDim),
    onSurface = colorResource(R.color.darkOnSurface),
    onSurfaceVariant = colorResource(R.color.darkOnSurfaceVariant),
    inverseSurface = colorResource(R.color.darkInverseSurface),
    inverseOnSurface = colorResource(R.color.darkInverseOnSurface),
    inversePrimary = colorResource(R.color.darkInversePrimary),
  )
