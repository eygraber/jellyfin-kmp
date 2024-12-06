# Testing

## Screenshot Tests

We use [Paparazzi] for screenshot testing.

To record screenshots for all screenshot tests in the project you can run:

```
./gradlew recordPaparazziDevDebug
```

To only record screenshots for a specific module, append the module path to the task, e.g.:

```
./gradlew :ui:welcome:recordPaparazziDevDebug
```

The same applies for verifying screenshot tests:

```
./gradlew verifyPaparazziDevDebug
```

or

```
./gradlew :ui:welcome:verifyPaparazziDevDebug
```

### Cleaning

To clean old screenshots you can run:

```
./gradlew cleanPaparazziDevDebug
```

If you made a lot of changes you can clean old screenshots and record new ones in the same task by running:

```
./gradlew cleanRecordPaparazziDevDebug
```

[Paparazzi]: https://github.com/cashapp/paparazzi
