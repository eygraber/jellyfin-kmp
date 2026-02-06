package com.eygraber.jellyfin.test.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.v2.createComposeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule

/**
 * A base class that can be used for performing Compose-layer testing using Robolectric, Compose
 * Testing, and JUnit 4.
 */
@Suppress("AbstractClassCanBeConcreteClass") // not meant to be used concretely
abstract class BaseComposeTest : BaseRobolectricTest {
  @OptIn(ExperimentalCoroutinesApi::class)
  protected val dispatcher = UnconfinedTestDispatcher()

  @OptIn(ExperimentalTestApi::class)
  @get:Rule
  val composeTestRule = createComposeRule(effectContext = dispatcher)
}

@Suppress("AbstractClassCanBeConcreteClass") // not meant to be used concretely
abstract class ComposeIntentTest : BaseComposeTest() {
  class StateHolder<Intent, ViewState>(
    val state: ViewState,
  ) {
    var lastIntent: Intent? = null
  }

  @Suppress("ImplicitUnitReturnType")
  inline fun <Intent, ViewState> runIntentTest(
    state: ViewState,
    crossinline view: @Composable (ViewState, (Intent) -> Unit) -> Unit,
    crossinline block: suspend StateHolder<Intent, ViewState>.() -> Unit,
  ) = runTest {
    StateHolder<Intent, ViewState>(state).apply {
      composeTestRule.setContent {
        view(
          state,
        ) { intent ->
          lastIntent = intent
        }
      }

      block()
    }
  }
}
