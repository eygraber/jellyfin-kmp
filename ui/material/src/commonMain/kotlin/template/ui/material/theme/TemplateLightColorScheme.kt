package template.ui.material.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun templateLightColorScheme(): ColorScheme =
  lightColorScheme(
    primary = TemplateColors.lightPrimary,
    onPrimary = TemplateColors.lightOnPrimary,
    primaryContainer = TemplateColors.lightPrimaryContainer,
    onPrimaryContainer = TemplateColors.lightOnPrimaryContainer,
    secondary = TemplateColors.lightSecondary,
    onSecondary = TemplateColors.lightOnSecondary,
    secondaryContainer = TemplateColors.lightSecondaryContainer,
    onSecondaryContainer = TemplateColors.lightOnSecondaryContainer,
    tertiary = TemplateColors.lightTertiary,
    onTertiary = TemplateColors.lightOnTertiary,
    tertiaryContainer = TemplateColors.lightTertiaryContainer,
    onTertiaryContainer = TemplateColors.lightOnTertiaryContainer,
    error = TemplateColors.lightError,
    onError = TemplateColors.lightOnError,
    errorContainer = TemplateColors.lightErrorContainer,
    onErrorContainer = TemplateColors.lightOnErrorContainer,
    background = TemplateColors.lightBackground,
    onBackground = TemplateColors.lightOnBackground,
    surface = TemplateColors.lightSurface,
    surfaceVariant = TemplateColors.lightSurfaceVariant,
    surfaceContainerLowest = TemplateColors.lightSurfaceContainerLowest,
    surfaceContainerLow = TemplateColors.lightSurfaceContainerLow,
    surfaceContainer = TemplateColors.lightSurfaceContainer,
    surfaceContainerHigh = TemplateColors.lightSurfaceContainerHigh,
    surfaceContainerHighest = TemplateColors.lightSurfaceContainerHighest,
    surfaceBright = TemplateColors.lightSurfaceBright,
    surfaceDim = TemplateColors.lightSurfaceDim,
    onSurface = TemplateColors.lightOnSurface,
    onSurfaceVariant = TemplateColors.lightOnSurfaceVariant,
    inverseSurface = TemplateColors.lightInverseSurface,
    inverseOnSurface = TemplateColors.lightInverseOnSurface,
    inversePrimary = TemplateColors.lightInversePrimary,
  )
