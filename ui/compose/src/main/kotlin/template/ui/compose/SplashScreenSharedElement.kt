package template.ui.compose

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.vice.nav.LocalAnimatedVisibilityScope
import com.eygraber.vice.nav.LocalSharedTransitionScope
import com.eygraber.vice.nav.rememberSharedContentState
import com.eygraber.vice.nav.sharedElement

@OptIn(ExperimentalSharedTransitionApi::class)
@Suppress("ModifierComposable")
@Composable
fun Modifier.sharedSplashScreenIcon() =
  requiredSize(288.dp)
    .sharedElement(
      sharedTransitionScope = LocalSharedTransitionScope.current,
      animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
      boundsTransform = { _, _ -> tween(durationMillis = 650) },
      state = rememberSharedContentState("SplashScreenIcon"),
    )
