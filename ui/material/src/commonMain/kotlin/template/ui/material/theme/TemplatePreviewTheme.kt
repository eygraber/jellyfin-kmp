package template.ui.material.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.eygraber.vice.nav.LocalAnimatedVisibilityScope
import com.eygraber.vice.nav.LocalSharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TemplatePreviewTheme(
  isDarkMode: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  TemplateTheme(isDarkMode = isDarkMode) {
    SharedTransitionLayout {
      CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
      ) {
        AnimatedVisibility(
          visible = true,
        ) {
          CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this,
            content = content,
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateModalBottomSheetPreviewTheme(
  isDarkMode: Boolean = isSystemInDarkTheme(),
  skipPartiallyExpanded: Boolean = false,
  initialValue: SheetValue = SheetValue.PartiallyExpanded,
  properties: ModalBottomSheetProperties = defaultModalBottomSheetProperties(isDarkMode),
  content: @Composable ColumnScope.() -> Unit,
) {
  TemplatePreviewTheme(
    isDarkMode = isDarkMode,
  ) {
    ModalBottomSheetPreview(
      skipPartiallyExpanded = skipPartiallyExpanded,
      initialValue = initialValue,
      properties = properties,
      content = content,
    )
  }
}

@Composable
expect fun TemplateEdgeToEdgePreviewTheme(
  isDarkMode: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateEdgeToEdgeModalBottomSheetPreviewTheme(
  isDarkMode: Boolean = isSystemInDarkTheme(),
  skipPartiallyExpanded: Boolean = false,
  initialValue: SheetValue = SheetValue.PartiallyExpanded,
  properties: ModalBottomSheetProperties = defaultModalBottomSheetProperties(isDarkMode),
  content: @Composable ColumnScope.() -> Unit,
) {
  TemplateEdgeToEdgePreviewTheme(
    isDarkMode = isDarkMode,
  ) {
    ModalBottomSheetPreview(
      skipPartiallyExpanded = skipPartiallyExpanded,
      initialValue = initialValue,
      properties = properties,
      content = content,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalBottomSheetPreview(
  skipPartiallyExpanded: Boolean,
  initialValue: SheetValue,
  properties: ModalBottomSheetProperties,
  content: @Composable ColumnScope.() -> Unit,
) {
  val sheetState = remember {
    SheetState(
      skipPartiallyExpanded = skipPartiallyExpanded,
      positionalThreshold = { 1F },
      velocityThreshold = { 1F },
      initialValue = initialValue,
      skipHiddenState = false,
      confirmValueChange = { true },
    )
  }

  LaunchedEffect(sheetState) {
    if(initialValue == SheetValue.Expanded) {
      sheetState.expand()
    }
    else {
      sheetState.partialExpand()
    }
  }

  ModalBottomSheet(
    onDismissRequest = {},
    sheetState = sheetState,
    properties = properties,
    content = content,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun defaultModalBottomSheetProperties(
  isDarkMode: Boolean,
) = ModalBottomSheetProperties(
  isAppearanceLightNavigationBars = !isDarkMode,
  isAppearanceLightStatusBars = !isDarkMode,
)
