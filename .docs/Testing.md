# Testing

## Detailed Documentation

See [testing/](testing/) for comprehensive testing documentation:
- [testing/unit-tests.md](testing/unit-tests.md) - JUnit, fakes, basic testing
- [testing/model-tests.md](testing/model-tests.md) - MVI Model testing patterns
- [testing/flow-tests.md](testing/flow-tests.md) - Turbine, Flow testing
- [testing/screenshot-tests.md](testing/screenshot-tests.md) - Paparazzi patterns
- [testing/repository-tests.md](testing/repository-tests.md) - Data layer testing

## Quick Reference

```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run module tests
./gradlew :screens:welcome:testDebugUnitTest

# Screenshot tests - record
./gradlew recordPaparazziDebug

# Screenshot tests - verify
./gradlew verifyPaparazziDebug
```

## Screenshot Tests

We use [Paparazzi] for screenshot testing.

### Recording

```bash
# Record all
./gradlew recordPaparazziDebug

# Record specific module
./gradlew :screens:welcome:recordPaparazziDebug
```

### Verifying

```bash
# Verify all
./gradlew verifyPaparazziDebug

# Verify specific module
./gradlew :screens:welcome:verifyPaparazziDebug
```

### Cleaning

```bash
# Clean old screenshots
./gradlew cleanPaparazziDebug

# Clean and re-record
./gradlew cleanRecordPaparazziDebug
```

[Paparazzi]: https://github.com/cashapp/paparazzi
