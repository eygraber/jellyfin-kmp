# Testing Strategy

Testing strategies for Template.

## Contents

- [unit-tests.md](unit-tests.md) - JUnit, fakes, basic testing
- [model-tests.md](model-tests.md) - MVI Model testing patterns
- [flow-tests.md](flow-tests.md) - Turbine, Flow testing
- [screenshot-tests.md](screenshot-tests.md) - Paparazzi patterns
- [repository-tests.md](repository-tests.md) - Data layer testing

## Philosophy

1. **Prefer fakes over mocks** - Create fake implementations
2. **Test behavior, not implementation** - Focus on what, not how
3. **Use Kotest assertions** - `shouldBe`, `shouldNotBe`
4. **Test edge cases** - Not just happy path

## Frameworks

| Framework       | Purpose                                 |
|-----------------|-----------------------------------------|
| JUnit 4         | Primary test runner for android modules |
| Robolectric     | Android framework dependencies          |
| Kotest          | Assertions                              |
| Turbine         | Flow testing                            |
| Paparazzi       | Screenshot tests                        |
| ComposeTestRule | Compose UI tests                        |
| JUnit 5         | Primary test runner for JVM modules     |
| Konsist         | Test framework for project structure    |

## Commands

```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run module tests
./gradlew :screens:my-screen:testDebugUnitTest

# Screenshot tests
./gradlew verifyPaparazziDebug
./gradlew recordPaparazziDebug

# Run konsist tests
./gradlew :konsist:test

# All checks
./check
```

## Test Location

All tests: `src/commonTest/kotlin/` in their respective modules.
