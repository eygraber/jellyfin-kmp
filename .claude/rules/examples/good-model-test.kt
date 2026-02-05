// Exemplar Model Tests following all project conventions
// See .claude/rules/testing.md for complete rules

package com.eygraber.jellyfin.screens.example.models

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.example.FakeExampleRepository
import com.eygraber.jellyfin.test.utils.BaseRobolectricTest
import com.eygraber.vice.loadable.isLoaded
import com.eygraber.vice.loadable.isLoading
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

// Pattern 1: Composable State Model (Molecule + Turbine)
@RunWith(AndroidJUnit4::class)
class ExampleLocalesModelTest {
  @Test
  fun `locales are properly loaded from data source`() = runTest {
    val model = RealExampleLocalesModel(
      context = ApplicationProvider.getApplicationContext(),
      repository = FakeExampleRepository(),
    )

    moleculeFlow(RecompositionMode.Immediate) {
      model.currentState()
    }.test {
      // ✅ Check loading state
      awaitItem().isLoading shouldBe true

      // ✅ Check loaded state
      val loaded = awaitItem()
      loaded.isLoaded shouldBe true
      loaded.value.size shouldBe 3

      ensureAllEventsConsumed()
    }
  }
}

// Pattern 2: Suspended Model with Side Effects
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ExampleVerificationModelTest {
  @Test
  fun `when verification succeeds, side effect is triggered`() =
    runTestWithSubject(
      onSideEffect = { wasCalled = true },
    ) { scope ->
      verify("123456")

      scope.runCurrent()

      wasCalled shouldBe true
    }

  @Test
  fun `when verification succeeds, returns true`() =
    runTestWithSubject {
      verify("123456") shouldBe true
    }

  @Test
  fun `when verification fails, returns false`() =
    runTestWithSubject(
      verifyResult = JellyfinResult.Error(),
    ) {
      verify("123456") shouldBe false
    }

  // ✅ Helper function with configurable fakes
  private inline fun runTestWithSubject(
    noinline onSideEffect: () -> Unit = {},
    verifyResult: JellyfinResult<String> = JellyfinResult.Success(""),
    crossinline block: suspend ExampleVerificationModel.(TestScope) -> Unit,
  ): TestResult = runTest {
    val repository = FakeExampleRepository(
      onSideEffect = onSideEffect,
      verifyResult = verifyResult,
    )

    RealExampleVerificationModel(
      context = ApplicationProvider.getApplicationContext(),
      repository = repository,
    ).block(this)
  }
}

// Pattern 3: Simple Stateful Model
class ExampleDialogModelTest {
  @Test
  fun `initial value matches key parameter`() {
    val model = createModel(
      key = ExampleKey(isVisible = true),
    )

    model.value shouldBe true
  }

  @Test
  fun `dismiss updates state to false`() = runTest {
    val model = createModel(key = ExampleKey(isVisible = true))

    model.dismiss()

    model.value shouldBe false
  }

  @Test
  fun `dismiss clears repository flag`() = runTest {
    val repository = FakeExampleRepository(flag = true)
    val model = createModel(repository = repository)

    model.dismiss()

    // ✅ Verify side effect via fake's state
    repository.flag shouldBe false
  }

  private fun createModel(
    key: ExampleKey = ExampleKey(isVisible = false),
    repository: ExampleRepository = FakeExampleRepository(),
  ) = RealExampleDialogModel(
    key = key,
    repository = repository,
  )
}

// Pattern 4: Testing Repository State Changes
@RunWith(AndroidJUnit4::class)
class ExamplePersistModelTest : BaseRobolectricTest() {
  @Test
  fun `persist returns success and updates repository`() =
    runTestWithSubject(
      updateResult = JellyfinResult.Success(Unit),
    ) { subject, repository ->
      val result = subject.persist(testData)

      result shouldBe JellyfinResult.Success()
      repository.persistedData shouldBe testData
    }

  @Test
  fun `persist returns failure and does not update repository`() =
    runTestWithSubject(
      updateResult = JellyfinResult.Error(),
    ) { subject, repository ->
      val result = subject.persist(testData)

      result shouldBe JellyfinResult.Error()
      repository.persistedData shouldBe null
    }

  private fun runTestWithSubject(
    updateResult: JellyfinResult<Unit>,
    testBody: suspend (subject: ExamplePersistModel, repository: FakeExampleRepository) -> Unit,
  ): TestResult = runTest {
    val repository = FakeExampleRepository(updateResult = updateResult)
    val subject = RealExamplePersistModel(
      context = ApplicationProvider.getApplicationContext(),
      repository = repository,
    )
    testBody(subject, repository)
  }

  companion object {
    private val testData = ExampleData(id = "123", name = "Test")
  }
}

// Pattern 5: Testing with MutableStateFlow Dependencies
@RunWith(AndroidJUnit4::class)
class ExampleItemsModelTest : BaseRobolectricTest() {
  @Test
  fun `when data updates, model state reflects changes`() = runTest {
    val dataFlow = MutableStateFlow(listOf(item1, item2))
    val repository = FakeExampleRepository(itemsFlow = dataFlow)
    val model = RealExampleItemsModel(repository)

    moleculeFlow(RecompositionMode.Immediate) {
      model.currentState()
    }.test {
      awaitItem().isLoading shouldBe true

      val loaded = awaitItem()
      loaded.isLoaded shouldBe true
      loaded.value shouldBe listOf(item1, item2)

      // Update flow
      dataFlow.value = listOf(item1, item2, item3)

      val updated = awaitItem()
      updated.value shouldBe listOf(item1, item2, item3)

      cancel()
    }
  }
}

// Key Takeaways:
// ✅ Always test Real* implementations
// ✅ Use Fake*Repository, never mocks
// ✅ Use AndroidJUnit4 runner for Android SDK dependencies
// ✅ Use BaseRobolectricTest when Context is needed
// ✅ Use moleculeFlow + turbine for @Composable state
// ✅ Use runTest for suspend functions
// ✅ Test behavior (inputs → outputs + side effects)
// ✅ Use descriptive backtick test names
// ✅ Verify side effects via fake's state, not mock verification
