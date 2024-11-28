package template.destinations.root

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.DestinationScope

@Inject
@SingleIn(DestinationScope::class)
class RootCompositor : ViceCompositor<RootIntent, RootViewState> {
  @Composable
  override fun composite() = RootViewState

  override suspend fun onIntent(intent: RootIntent) {}
}
