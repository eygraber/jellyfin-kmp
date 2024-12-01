package template.nav.dev

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties

@OptIn(ExperimentalMaterial3Api::class)
internal actual fun platformModalBottomSheetProperties(isDarkMode: Boolean) =
  ModalBottomSheetProperties(
    isAppearanceLightNavigationBars = !isDarkMode,
    isAppearanceLightStatusBars = !isDarkMode,
  )
