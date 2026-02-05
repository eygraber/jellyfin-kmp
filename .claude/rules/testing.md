---
paths:
  - "**/test/**/*.kt"
---

# Testing Strategy
Write comprehensive tests: unit tests, flow tests, Intent tests, OnIntent tests, screenshot tests
Run tests before committing: ./gradlew testDebugUnitTest
Unit tests should be in src/test/kotlin directory
Name test files with Test suffix (e.g., MyClassTest.kt)
Intent tests = View UI interactions → Intent emissions (ComposeTestRule)
OnIntent tests = Compositor Intent handling → Model/Effects coordination (runTest)

# Assertions and Frameworks
Use Kotest for all assertions (e.g., result shouldBe expected)
❌ Bad: assertEquals(expected, result)
✅ Good: result shouldBe expected

Use JUnit 4 as the primary testing framework for Android modules
Use Robolectric when code has Android framework dependencies
Extend BaseRobolectricTest when tests need Context or Android framework components
Use @RunWith(AndroidJUnit4::class) for Robolectric tests
Use JUnit 6 as the primary testing framework for JVM modules

# Mocking and Fakes
🔴 CRITICAL: Avoid MockK unless absolutely necessary - strongly prefer fake implementations
❌ Bad: val repo = mockk<UserRepository>()
✅ Good: val repo = FakeUserRepository()

Create fakes for dependencies instead of mocking whenever possible

# Flow Testing
Use Turbine for testing Flows - awaitItem(), awaitComplete(), awaitError()
Example: dataSource.flow.test { awaitItem() shouldBe expectedValue }
Use TestSubjectCoordinator for complex time-sensitive Flow tests

# Intent Testing (View Layer)
Intent tests verify View interactions trigger correct Intents
Prefer ComposeIntentTest for most Intent tests (provides runIntentTest helper and lastIntent tracking)
Use BaseComposeTest when you need ComposeTestRule but not Intent test helpers
Create TestRobot implementations for complex screen Intent tests
Focus on UI behavior: clicks, text input, gestures → Intent emissions

# OnIntent Testing (Compositor Layer)
OnIntent tests verify Compositor handles Intents correctly by coordinating Model and Effects
Test business logic: navigation, data loading, side effects
Use runTest for coroutine scope in all OnIntent tests
Use Turbine to test Model state changes via Compositor
Use fake dependencies (FakeRepository, FakeNavigator) instead of mocks

# Screenshot Testing
Use Paparazzi for screenshot tests - run recordPaparazzi to generate golden images

Two modes of screenshot tests:
1. Screen tests (:screens modules) - test full screens with ViewStatePreviewProvider + PaparazziDeviceConfig
2. Component tests (:ui modules) - test individual UI components with custom test methods

Screen tests use SuperDoEdgeToEdgePreviewTheme and device configs for multiple variations
Component tests use SuperDoPreviewTheme and test specific component states/behaviors
Component tests may reuse preview functions when components need complex setup
Wrap content with SuperDoPreviewAsyncImageProvider if screen/component uses Coil for image loading

# Model Testing Patterns
Model tests verify business logic in Model implementations using fake repositories

**Key Principles:**
- Test Real* implementations, not interfaces
- Use Fake*Repository for dependencies, not mocks
- Test behavior: inputs → outputs + side effects
- Use descriptive backtick test names

**Pattern Selection by Model Type:**
1. Composable State Models (@Composable currentState()) → moleculeFlow + turbine
2. Suspended Models (suspend fun) → runTest + TestScope
3. Stateful Models (properties) → direct property access + runTest for methods
4. Compose UI Models → BaseComposeTest + composeTestRule.setContent
5. Complex async → TestSubjectCoordinator + moleculeFlow

**Common Tools:**
- moleculeFlow(RecompositionMode.Immediate) for @Composable state
- turbine.test { awaitItem(), proceed(), cancel() } for Flow testing
- TestLifecycleOwner + LocalLifecycleOwner for lifecycle-aware models
- TestScope.runCurrent() to advance suspended operations
- FakeRepository implementations track state and behavior

**Robolectric Requirements:**
- Use @RunWith(AndroidJUnit4::class) for Android SDK dependencies
- Extend appropriate base class: ComposeIntentTest > BaseComposeTest > BaseRobolectricTest
- ComposeIntentTest: Intent tests with runIntentTest helper
- BaseComposeTest: Compose tests needing composeTestRule
- BaseRobolectricTest: Non-Compose tests needing Android framework
- ApplicationProvider.getApplicationContext() for Context

### Documentation Reference
For complete patterns: .docs/testing/model-tests.md
