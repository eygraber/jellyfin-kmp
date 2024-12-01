package template.destinations.root

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import me.tatarka.inject.annotations.Inject

@Inject
class RootCompositor : ViceCompositor<RootIntent, RootViewState> {
  @Composable
  override fun composite() = RootViewState

  override suspend fun onIntent(intent: RootIntent) {}
}
