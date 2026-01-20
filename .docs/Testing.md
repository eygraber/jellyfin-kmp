# Testing

## Screenshot Tests

We use [Paparazzi] for screenshot testing.

To record screenshots for all screenshot tests in the project you can run:

```
./gradlew recordPaparazziDebug
```

To only record screenshots for a specific module, append the module path to the task, e.g.:

```
./gradlew :ui:welcome:recordPaparazziDebug
```

The same applies for verifying screenshot tests:

```
./gradlew verifyPaparazziDebug
```

or

```
./gradlew :ui:welcome:verifyPaparazziDebug
```

### Cleaning

To clean old screenshots you can run:

```
./gradlew cleanPaparazziDebug
```

If you made a lot of changes you can clean old screenshots and record new ones in the same task by running:

```
./gradlew cleanRecordPaparazziDebug
```

[Paparazzi]: https://github.com/cashapp/paparazzi
