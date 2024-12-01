package template.ui.material.theme

import androidx.compose.ui.graphics.Color

object TemplateColors {
  val lightPrimary = Color(0XFF6750A4)
  val lightOnPrimary = Color(0xFFFFFFFF)
  val lightPrimaryContainer = lightPrimary
  val lightOnPrimaryContainer = lightOnPrimary
  val lightSecondary = Color(0xFF00CCA7)
  val lightOnSecondary = Color(0xFFFFFFFF)
  val lightSecondaryContainer = Color(0XFFE8DEF8)
  val lightOnSecondaryContainer = Color(0xFFFFFFFF)
  val lightTertiary = Color(0xFFA49AFF)
  val lightOnTertiary = Color(0xFFFFFFFF)
  val lightTertiaryContainer = Color(0xFF25A3F8)
  val lightOnTertiaryContainer = Color(0xFFFFFFFF)
  val lightError = Color(0xFFED3227)
  val lightOnError = Color(0xFFFFFFFF)
  val lightErrorContainer = Color(0xFFED3227)
  val lightOnErrorContainer = Color(0xFFFFFFFF)
  val lightBackground get() = lightSurface
  val lightOnBackground get() = lightOnSurface
  val lightSurface = Color(0xFFF2F2F7)
  val lightSurfaceVariant = Color(0xFFE1E1E0)
  val lightOnSurface = Color.Black
  val lightOnSurfaceVariant = Color.Black
  val lightSurfaceContainerLowest = Color(0xFFFFFFFF)
  val lightSurfaceContainerLow = Color(0xFFFFFFFF)
  val lightSurfaceContainer = Color(0xFFFFFFFF)
  val lightSurfaceContainerHigh = Color(0xFFFFFFFF)
  val lightSurfaceContainerHighest = Color(0xFFFFFFFF)
  val lightSurfaceBright = lightSurfaceContainerLowest
  val lightSurfaceDim = lightSurfaceContainerHighest
  val lightInverseSurface get() = darkSurface
  val lightInverseOnSurface get() = darkOnSurface
  val lightInversePrimary get() = darkPrimary

  val darkPrimary = Color(0XFFD0BCFF)
  val darkOnPrimary = Color.Black
  val darkPrimaryContainer = darkPrimary
  val darkOnPrimaryContainer = darkOnPrimary
  val darkSecondary = Color(0xFF44E5C8)
  val darkOnSecondary = Color(0xFFFFFFFF)
  val darkSecondaryContainer = Color(0xFFFFFFFF)
  val darkOnSecondaryContainer = Color(0XFF4A4458)
  val darkTertiary = Color(0xFFEBEBFF)
  val darkOnTertiary = Color(0xFFFFFFFF)
  val darkTertiaryContainer = Color(0xFF62BEFC)
  val darkOnTertiaryContainer = Color(0xFFFFFFFF)
  val darkError = Color(0xFFFF594F)
  val darkOnError = Color(0xFFFFFFFF)
  val darkErrorContainer = Color(0xFFFF594F)
  val darkOnErrorContainer = Color(0xFFFFFFFF)

  // if this is changed, make sure to change the
  // windowSplashScreenBackground attributes in Theme.TemplateSplash
  val darkBackground get() = darkSurface
  val darkOnBackground get() = darkOnSurface
  val darkSurface = Color(0xFF000000)
  val darkSurfaceVariant = Color(0xFF262626)
  val darkSurfaceContainerLowest = Color(0xFF1C1C1E)
  val darkSurfaceContainerLow = Color(0xFF1C1C1E)
  val darkSurfaceContainer = Color(0xFF1F2124)
  val darkSurfaceContainerHigh = Color(0xFF1F2124)
  val darkSurfaceContainerHighest = Color(0xFF1F2124)
  val darkSurfaceBright = darkSurfaceContainerHighest
  val darkSurfaceDim = darkSurfaceContainerLowest
  val darkOnSurface = Color(0xFFFFFFFF)
  val darkOnSurfaceVariant = Color(0xFFFFFFFF)
  val darkInverseSurface = lightSurface
  val darkInverseOnSurface = lightOnSurface
  val darkInversePrimary = lightPrimary
}
