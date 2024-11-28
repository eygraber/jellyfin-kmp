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

[Paparazzi]: https://github.com/cashapp/paparazzi
