---
name: test
description: Generate tests for Android code - unit tests, model tests, flow tests, intent tests, and more.
argument-hint: "[type] [file] - e.g., 'model MyModel.kt', 'flow', 'unit'"
context: fork
allowed-tools: Read, Edit, Write, Glob, Grep, Bash(./gradlew *, ./check, grep)
---

# Test Writer

Generate comprehensive tests following project patterns and conventions.

## Usage

```
/test MyModel.kt                  # Auto-detect test type from file
/test model MyModel.kt            # Generate Model tests (6 patterns)
/test flow DataSource.kt          # Generate Flow/Turbine tests
/test intent MyScreenView.kt      # Generate Intent emission tests
/test onintent MyCompositor.kt    # Generate OnIntent handling tests
/test repository MyRepository.kt  # Generate repository tests
/test unit MyHelper.kt            # Generate basic unit tests
```

For screenshot tests, use `/screenshot-tests` skill.

## Test Types

| Type         | Focus                            |
|--------------|----------------------------------|
| `model`      | MVI Model state, business logic  |
| `flow`       | Reactive streams with Turbine    |
| `intent`     | UI interaction → Intent emission |
| `onintent`   | Compositor Intent handling       |
| `repository` | Data layer coordination          |
| `unit`       | Simple utilities, helpers        |

## Auto-Detection

When no type specified, detect from file:

| File Pattern             | Test Type  |
|--------------------------|------------|
| `*Model.kt`              | model      |
| `*Compositor.kt`         | onintent   |
| `*View.kt`               | intent     |
| `*Repository.kt`         | repository |
| `*Source.kt`, `*Flow.kt` | flow       |
| Other                    | unit       |

## Process

1. **Read** the source file to understand what needs testing
2. **Identify** the appropriate test type and pattern
3. **Find** existing tests and fakes in the module
4. **Generate** tests following the pattern
5. **Run** tests to verify they pass
6. **Fix** any compilation or test failures

## Core Conventions

### Test Structure (AAA)
```kotlin
@Test
fun `descriptive name with backticks`() {
  // Arrange - set up test state
  val subject = createSubject()

  // Act - perform the action
  val result = subject.doSomething()

  // Assert - verify outcome
  result shouldBe expected
}
```

### Naming
- Use backticks for readable test names
- Describe behavior: `when X happens, then Y`
- Be specific about conditions and outcomes

### Fakes Over Mocks
```kotlin
// GOOD: Fake with controllable behavior
class FakeRepository : MyRepository {
  var dataToReturn: Data? = null
  var shouldFail = false

  override suspend fun getData() = when {
    shouldFail -> JellyfinResult.Failure(Exception())
    else -> JellyfinResult.Success(dataToReturn!!)
  }
}

// BAD: Avoid MockK unless absolutely necessary
```

### Assertions (Kotest)
```kotlin
result shouldBe expected
result shouldNotBe null
list shouldContain item
list shouldHaveSize 3
```

## Test Location

```
src/commonTest/kotlin/           # Unit, Model, Flow, OnIntent tests
  com/jellyfin/...
    RealMyModelTest.kt     # Test Real* implementations
    FakeMyRepository.kt    # Fakes for testing
```

## Running Tests

```bash
./gradlew :module:testDebugUnitTest     # Run module tests
./gradlew testDebugUnitTest             # Run all tests
./check                                  # Full quality suite
```

## Documentation

- [.docs/testing/](/.docs/testing) - Complete testing guide
