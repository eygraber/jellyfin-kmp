---
name: check
description: Run quality checks (lint, detekt, tests, formatting) and fix any failures.
argument-hint: "[tool] or blank for full suite"
context: fork
allowed-tools: Read, Edit, Bash(./check, ./format, ./detekt, ./gradlew *, grep, cat)
---

# Run Quality Checks

Run static analysis, tests, and formatters. Fix any failures found.

## Modes

### Full Suite (default)
```bash
./check
```
Runs: formatting, detekt, lint, unit tests, screenshot tests, konsist.

### Individual Tools

| Tool              | Command                               | Purpose                            |
|-------------------|---------------------------------------|------------------------------------|
| Format (auto-fix) | `./format`                            | Auto-fix formatting (ktlint)       |
| Format            | `./format --no-format`                | Report formatting issues (ktlint)  |
| Detekt            | `./detekt`                            | Static analysis                    |
| Lint              | `./gradlew :apps:android:lintDebug`   | Android-specific issues            |
| Unit Tests        | `./gradlew testDebugUnitTest`         | All unit tests                     |
| Module Tests      | `./gradlew :module:testDebugUnitTest` | Single module                      |
| Screenshots       | `./gradlew verifyPaparazziDebug`      | Screenshot tests                   |
| Konsist           | `./gradlew :konsist:test`             | Structural and architectural tests |
| Build Health      | `./gradlew buildHealth`               | Dependency analysis                |

#### Formatting

Formatting issues that can't be auto-fixed (e.g. `max-line-length`, issues in `build.gradle.kts` files, etc...) won't
get reported by `./format` unless you specify the `--no-format` flag.

## Workflow

1. **Run** the requested check (or full suite if unspecified)
2. **Analyze** failures from output
3. **Fix** issues directly - formatting, code fixes, test fixes
4. **Re-run** to verify fixes
5. **Report** results

## Fix Philosophy

- **Always fix** the reported issues
- **Never suppress** warnings without explicit user approval
- **Never refactor** or do major changes without user approval
- **Ask first** if a fix requires significant code changes

## Common Fixes

| Issue                    | Fix                                                       |
|--------------------------|-----------------------------------------------------------|
| Formatting               | `./format` auto-fixes                                     |
| Missing translations     | Use `/translate` skill                                    |
| Unused resources         | Remove them                                               |
| Hardcoded strings        | Extract to resources                                      |
| Konsist annotation order | Reorder: `@Inject` → `@SingleIn` → `@ContributesBinding`  |
| api vs implementation    | Change to correct configuration                           |
| Screenshot diff          | `./gradlew recordPaparazziDebug` if change is intentional |

## Priority

1. **Critical**: Build failures, security issues
2. **High**: Failing tests, correctness issues
3. **Medium**: Static analysis warnings
4. **Low**: Style issues (usually auto-fixed)

Ultimately, all reported issues need to be resolved.

## Checklist

- [ ] All checks pass
- [ ] No suppressions added
- [ ] Format run after fixes
- [ ] Tests still pass after code changes
