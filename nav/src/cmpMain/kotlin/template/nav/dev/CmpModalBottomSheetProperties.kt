package template.nav.dev

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetDefaults

@OptIn(ExperimentalMaterial3Api::class)
internal actual fun platformModalBottomSheetProperties(isDarkMode: Boolean) =
  ModalBottomSheetDefaults.properties
