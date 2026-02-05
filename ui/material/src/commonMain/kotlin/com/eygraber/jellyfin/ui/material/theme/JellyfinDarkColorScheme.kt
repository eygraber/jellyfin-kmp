package com.eygraber.jellyfin.ui.material.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun jellyfinDarkColorScheme(): ColorScheme =
  darkColorScheme(
    primary = JellyfinColors.darkPrimary,
    onPrimary = JellyfinColors.darkOnPrimary,
    primaryContainer = JellyfinColors.darkPrimaryContainer,
    onPrimaryContainer = JellyfinColors.darkOnPrimaryContainer,
    secondary = JellyfinColors.darkSecondary,
    onSecondary = JellyfinColors.darkOnSecondary,
    secondaryContainer = JellyfinColors.darkSecondaryContainer,
    onSecondaryContainer = JellyfinColors.darkOnSecondaryContainer,
    tertiary = JellyfinColors.darkTertiary,
    onTertiary = JellyfinColors.darkOnTertiary,
    tertiaryContainer = JellyfinColors.darkTertiaryContainer,
    onTertiaryContainer = JellyfinColors.darkOnTertiaryContainer,
    error = JellyfinColors.darkError,
    onError = JellyfinColors.darkOnError,
    errorContainer = JellyfinColors.darkErrorContainer,
    onErrorContainer = JellyfinColors.darkOnErrorContainer,
    background = JellyfinColors.darkBackground,
    onBackground = JellyfinColors.darkOnBackground,
    surface = JellyfinColors.darkSurface,
    surfaceVariant = JellyfinColors.darkSurfaceVariant,
    surfaceContainerLowest = JellyfinColors.darkSurfaceContainerLowest,
    surfaceContainerLow = JellyfinColors.darkSurfaceContainerLow,
    surfaceContainer = JellyfinColors.darkSurfaceContainer,
    surfaceContainerHigh = JellyfinColors.darkSurfaceContainerHigh,
    surfaceContainerHighest = JellyfinColors.darkSurfaceContainerHighest,
    surfaceBright = JellyfinColors.darkSurfaceBright,
    surfaceDim = JellyfinColors.darkSurfaceDim,
    onSurface = JellyfinColors.darkOnSurface,
    onSurfaceVariant = JellyfinColors.darkOnSurfaceVariant,
    inverseSurface = JellyfinColors.darkInverseSurface,
    inverseOnSurface = JellyfinColors.darkInverseOnSurface,
    inversePrimary = JellyfinColors.darkInversePrimary,
  )
