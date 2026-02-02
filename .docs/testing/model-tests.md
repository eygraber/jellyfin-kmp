# Model Tests

Test Model business logic using real implementations and fake repositories.

## Model Types

Models in this codebase fall into several categories:

1. **Composable State Models** - Return `@Composable` state using Molecule
2. **Suspended Models** - Expose suspending functions
3. **Stateful Models** - Maintain internal state accessed via properties

## Pattern 1: Composable State Models (Molecule + Turbine)

Models with `@Composable fun currentState()` require Molecule and Turbine:

```kotlin
@RunWith(AndroidJUnit4::class)
class MyModelTest {
  @Test
  fun `data is properly loaded`() = runTest {
    val model = MyModelImpl(
      repository = FakeRepository(),
    )

    moleculeFlow(RecompositionMode.Immediate) {
      model.currentState()
    }.test {
      awaitItem().isLoading shouldBe true

      val loaded = awaitItem()
      loaded.isLoaded shouldBe true
      loaded.value.first() shouldBe ExpectedValue

      ensureAllEventsConsumed()
    }
  }
}
```

## Pattern 2: Suspended Models with Side Effects

Models with suspending functions that trigger side effects:

```kotlin
@RunWith(AndroidJUnit4::class)
class MyVerificationModelTest {
  @Test
  fun `when verification succeeds, returns true`() =
    runTestWithSubject {
      verify("123456") shouldBe true
    }

  @Test
  fun `when verification fails, returns false`() =
    runTestWithSubject(
      verifyResult = TemplateResult.Error(),
    ) {
      verify("123456") shouldBe false
    }

  private inline fun runTestWithSubject(
    verifyResult: TemplateResult<String> = TemplateResult.Success(""),
    crossinline block: suspend MyVerificationModel.() -> Unit,
  ): TestResult = runTest {
    val repository = FakeRepository(
      verifyResult = verifyResult,
    )

    RealMyVerificationModel(
      repository = repository,
    ).block()
  }
}
```

## Pattern 3: Simple Stateful Models

Models with properties and simple methods:

```kotlin
class MyDialogModelTest {
  @Test
  fun `initial value matches key parameter`() {
    val model = createModel(
      key = MyKey(isVisible = true),
    )

    model.value shouldBe true
  }

  @Test
  fun `dismiss updates state to false`() = runTest {
    val model = createModel(key = MyKey(isVisible = true))

    model.dismiss()

    model.value shouldBe false
  }

  private fun createModel(
    key: MyKey = MyKey(isVisible = false),
    repository: MyRepository = FakeRepository(),
  ) = RealMyDialogModel(
    key = key,
    repository = repository,
  )
}
```

## Pattern 4: Testing Return Values with Repository State

For models that return results and modify repository state:

```kotlin
@RunWith(AndroidJUnit4::class)
class MyPersistModelTest {
  @Test
  fun `persist returns success and updates repository`() =
    runTestWithSubject(
      updateResult = TemplateResult.Success(Unit),
    ) { subject, repository ->
      val result = subject.persist(data)

      result shouldBe TemplateResult.Success()
      repository.persistedData shouldBe data
    }

  @Test
  fun `persist returns failure and does not update repository`() =
    runTestWithSubject(
      updateResult = TemplateResult.Error(),
    ) { subject, repository ->
      val result = subject.persist(data)

      result shouldBe TemplateResult.Error()
      repository.persistedData shouldBe null
    }

  private fun runTestWithSubject(
    updateResult: TemplateResult<Unit>,
    testBody: suspend (subject: MyModel, repository: FakeRepository) -> Unit,
  ): TestResult = runTest {
    val repository = FakeRepository(updateResult = updateResult)
    val subject = RealMyModel(
      repository = repository,
    )
    testBody(subject, repository)
  }
}
```

## Guidelines

1. **Use real implementations** - Always test `Real*` implementations, not interfaces
2. **Use fake repositories** - Use `Fake*Repository` classes for dependencies
3. **Test behavior, not implementation** - Focus on inputs, outputs, and side effects
4. **Use descriptive test names** - Backtick names should describe scenario and outcome
5. **Robolectric for Android dependencies** - Use `@RunWith(AndroidJUnit4::class)` when Android SDK is needed
6. **Choose the right pattern** - Match the testing pattern to the model's interface
7. **Validate side effects** - Check repository state changes via fake implementations
