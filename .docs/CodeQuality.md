# Template Android Code Quality

There are several tools and checks run on every PR to ensure
a high standard of code quality in the project.

The `check` script will run all of the checks that are run for a PR
(arguments passed to `./check` will get forwarded to its invocation of `detekt`).
Passing `--lite` will run a subset of those checks.

## Android Lint

Android Lint should be run on the app module (`./gradlew :app:lintDevRelease`) as
opposed to each individual module, for performance purposes.

### template-lint

There is an included build in the project called `template-lint` where we keep our custom lint checks.

## Dependency Analysis Plugin

The [Dependency Analysis Plugin] is used to make sure that module dependencies are using the correct
configuration (`implementation` vs `api`), don't have any unused dependencies, and that there is no code
from a transitive dependencies present in the module's public API. 

## Detekt

[Detekt] is a static code analyzer used to maintain code quality. It runs in 2 modes:

1. No type resolution - You can run this mode using `./gradlew detekt`
2. Type resolution - The Kotlin compiler is used to get more information from the codebase to
run more advanced checks. The flavor of the app needs to be specified when running the task e.g.
`./gradle detektDebug detektRelease`.

There is a `detekt` script that will run both modes, using both the `debug` and `release`
flavors for type resolution. It will only run the `release` tasks on Gradle projects that have specific
`release` or `devRelease` sources (so that it runs faster).

Any arguments passed to `./detekt` will get forwarded
to the underlying Gradle invocation (e.g. passing `--continue` will tell Detekt to report all issues instead
of stopping after finding the first few issues).

## Formatting

[ktlint] is used to format the Kotlin files in the codebase.

Running the `format` script will automatically fix any violations that it finds;
pass `--no-format` to instead print a list of violations.

It will use the version of ktlint specified in `gradle/libs.versions.toml`,
downloading the binary if it isn't present locally.

It is configured via the `.editorconfig` at the root of the project.
`.editorconfig` files in child directories are overlaid on top of their parents,
and take effect for any matched Kotlin files in their subtree.

IDE formatting rules are checked into git at `.idea/codeStyles`.
This allows the IDE formatting tool to be used in a way that is _mostly_ compatible with `ktlint`
(there are rare cases where they disagree with each other; in those cases `ktlint` wins).

## Consistency

[Konsist] is used to keep patterns in the project consistent.

[Dependency Analysis Plugin]: https://github.com/autonomousapps/dependency-analysis-gradle-plugin
[Detekt]: https://detekt.dev/
[ktlint]: https://github.com/pinterest/ktlint
[Konsist]: https://github.com/LemonAppDev/konsist
