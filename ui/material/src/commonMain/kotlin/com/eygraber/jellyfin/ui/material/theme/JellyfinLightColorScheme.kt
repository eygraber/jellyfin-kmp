package com.eygraber.jellyfin.ui.material.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun jellyfinLightColorScheme(): ColorScheme =
  lightColorScheme(
    primary = JellyfinColors.lightPrimary,
    onPrimary = JellyfinColors.lightOnPrimary,
    primaryContainer = JellyfinColors.lightPrimaryContainer,
    onPrimaryContainer = JellyfinColors.lightOnPrimaryContainer,
    secondary = JellyfinColors.lightSecondary,
    onSecondary = JellyfinColors.lightOnSecondary,
    secondaryContainer = JellyfinColors.lightSecondaryContainer,
    onSecondaryContainer = JellyfinColors.lightOnSecondaryContainer,
    tertiary = JellyfinColors.lightTertiary,
    onTertiary = JellyfinColors.lightOnTertiary,
    tertiaryContainer = JellyfinColors.lightTertiaryContainer,
    onTertiaryContainer = JellyfinColors.lightOnTertiaryContainer,
    error = JellyfinColors.lightError,
    onError = JellyfinColors.lightOnError,
    errorContainer = JellyfinColors.lightErrorContainer,
    onErrorContainer = JellyfinColors.lightOnErrorContainer,
    background = JellyfinColors.lightBackground,
    onBackground = JellyfinColors.lightOnBackground,
    surface = JellyfinColors.lightSurface,
    surfaceVariant = JellyfinColors.lightSurfaceVariant,
    surfaceContainerLowest = JellyfinColors.lightSurfaceContainerLowest,
    surfaceContainerLow = JellyfinColors.lightSurfaceContainerLow,
    surfaceContainer = JellyfinColors.lightSurfaceContainer,
    surfaceContainerHigh = JellyfinColors.lightSurfaceContainerHigh,
    surfaceContainerHighest = JellyfinColors.lightSurfaceContainerHighest,
    surfaceBright = JellyfinColors.lightSurfaceBright,
    surfaceDim = JellyfinColors.lightSurfaceDim,
    onSurface = JellyfinColors.lightOnSurface,
    onSurfaceVariant = JellyfinColors.lightOnSurfaceVariant,
    inverseSurface = JellyfinColors.lightInverseSurface,
    inverseOnSurface = JellyfinColors.lightInverseOnSurface,
    inversePrimary = JellyfinColors.lightInversePrimary,
  )
