package template.nav

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

@OptIn(ExperimentalMaterial3Api::class)
@Immutable
data class BottomSheetSceneProperties(
  val modalBottomSheetModifier: Modifier = Modifier,
  val properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
  @Suppress("BooleanPropertyNaming")
  val skipPartiallyExpanded: Boolean = false,
  val confirmValueChange: (SheetValue) -> Boolean = { true },
  val contentWindowInsets: @Composable (SheetState) -> WindowInsets = defaultWindowInsets(),
  val dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
  val sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
  val tonalElevation: Dp = 0.dp,
  val shape: @Composable () -> Shape = { BottomSheetDefaults.ExpandedShape },
  val containerColor: @Composable () -> Color = { BottomSheetDefaults.ContainerColor },
  val contentColor: @Composable () -> Color = { contentColorFor(containerColor()) },
  val scrimColor: @Composable () -> Color = { BottomSheetDefaults.ScrimColor },
) {
  companion object {
    @OptIn(ExperimentalMaterial3Api::class)
    fun defaultWindowInsets(): @Composable (
      SheetState,
    ) -> WindowInsets = {
      BottomSheetDefaults.windowInsets
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
internal class BottomSheetScene<T : Any>(
  override val key: Any,
  override val previousEntries: List<NavEntry<T>>,
  override val overlaidEntries: List<NavEntry<T>>,
  private val entry: NavEntry<T>,
  private val properties: BottomSheetSceneProperties,
  private val onBack: () -> Unit,
) : OverlayScene<T> {
  override val entries: List<NavEntry<T>> = listOf(entry)

  override val content: @Composable (() -> Unit) = {
    val sheetState = rememberModalBottomSheetState(
      skipPartiallyExpanded = properties.skipPartiallyExpanded,
      confirmValueChange = properties.confirmValueChange,
    )

    ModalBottomSheet(
      onDismissRequest = { onBack() },
      sheetState = sheetState,
      modifier = properties.modalBottomSheetModifier,
      sheetMaxWidth = BottomSheetDefaults.SheetMaxWidth,
      shape = properties.shape(),
      properties = properties.properties,
      containerColor = properties.containerColor(),
      contentColor = properties.contentColor(),
      tonalElevation = properties.tonalElevation,
      scrimColor = properties.scrimColor(),
      dragHandle = properties.dragHandle,
      contentWindowInsets = {
        properties.contentWindowInsets(sheetState)
      },
    ) { entry.Content() }
  }
}

class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {
  override fun SceneStrategyScope<T>.calculateScene(
    entries: List<NavEntry<T>>,
  ): Scene<T>? {
    val lastEntry = entries.lastOrNull()
    val bottomSheetProperties = lastEntry?.metadata?.get(BOTTOM_SHEET_KEY) as? BottomSheetSceneProperties
    return bottomSheetProperties?.let { properties ->
      BottomSheetScene(
        key = lastEntry.contentKey,
        previousEntries = entries.dropLast(1),
        overlaidEntries = entries.dropLast(1),
        entry = lastEntry,
        properties = properties,
        onBack = onBack,
      )
    }
  }

  companion object {
    internal const val BOTTOM_SHEET_KEY = "bottom_sheet"

    @OptIn(ExperimentalMaterial3Api::class)
    fun bottomSheet(
      bottomSheetProperties: BottomSheetSceneProperties = BottomSheetSceneProperties(),
    ): Map<String, Any> = mapOf(BOTTOM_SHEET_KEY to bottomSheetProperties)
  }
}
