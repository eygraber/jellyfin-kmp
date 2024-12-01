package template.ui.material.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun templateDarkColorScheme(): ColorScheme =
  darkColorScheme(
    primary = TemplateColors.darkPrimary,
    onPrimary = TemplateColors.darkOnPrimary,
    primaryContainer = TemplateColors.darkPrimaryContainer,
    onPrimaryContainer = TemplateColors.darkOnPrimaryContainer,
    secondary = TemplateColors.darkSecondary,
    onSecondary = TemplateColors.darkOnSecondary,
    secondaryContainer = TemplateColors.darkSecondaryContainer,
    onSecondaryContainer = TemplateColors.darkOnSecondaryContainer,
    tertiary = TemplateColors.darkTertiary,
    onTertiary = TemplateColors.darkOnTertiary,
    tertiaryContainer = TemplateColors.darkTertiaryContainer,
    onTertiaryContainer = TemplateColors.darkOnTertiaryContainer,
    error = TemplateColors.darkError,
    onError = TemplateColors.darkOnError,
    errorContainer = TemplateColors.darkErrorContainer,
    onErrorContainer = TemplateColors.darkOnErrorContainer,
    background = TemplateColors.darkBackground,
    onBackground = TemplateColors.darkOnBackground,
    surface = TemplateColors.darkSurface,
    surfaceVariant = TemplateColors.darkSurfaceVariant,
    surfaceContainerLowest = TemplateColors.darkSurfaceContainerLowest,
    surfaceContainerLow = TemplateColors.darkSurfaceContainerLow,
    surfaceContainer = TemplateColors.darkSurfaceContainer,
    surfaceContainerHigh = TemplateColors.darkSurfaceContainerHigh,
    surfaceContainerHighest = TemplateColors.darkSurfaceContainerHighest,
    surfaceBright = TemplateColors.darkSurfaceBright,
    surfaceDim = TemplateColors.darkSurfaceDim,
    onSurface = TemplateColors.darkOnSurface,
    onSurfaceVariant = TemplateColors.darkOnSurfaceVariant,
    inverseSurface = TemplateColors.darkInverseSurface,
    inverseOnSurface = TemplateColors.darkInverseOnSurface,
    inversePrimary = TemplateColors.darkInversePrimary,
  )
