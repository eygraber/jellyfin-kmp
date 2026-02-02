package template.screens.root

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class RootCompositor : ViceCompositor<RootIntent, RootViewState> {
  @Composable
  override fun composite() = RootViewState

  override suspend fun onIntent(intent: RootIntent) {}
}
