Run ./check to execute all PR checks
Use ktlint for code formatting (configured via .editorconfig)
Run Android lint on app module for performance: ./gradlew :app:lintRelease
Use Detekt for static analysis (both with and without type resolution)
Never modify detekt.yml files without permission
detekt.yml files can be read to provide context on why a violation may be occurring
Do not run DAGP tasks (`buildHealth`, `projectHealth`) — they don't currently work with KMP modules in this project. Skip until validated with KMP.
Use Konsist tests to enforce architectural rules
Format violations should be fixed automatically with ./format script
Detekt runs in two modes: no type resolution and with type resolution
Pass --continue to detekt to report all issues instead of stopping early
IDE formatting rules are in .idea/codeStyles (mostly compatible with ktlint)
When ktlint and IDE disagree, ktlint wins

## Automated Enforcement

Claude Code hooks automatically run these tools after editing Kotlin files:
- `./format` runs after editing `.kt` or `.kts` files (async)
- `./detekt` runs after editing `.kt` source files (async, skips test files)

## Formatting before commit

The post-edit `./format` hook runs **asynchronously**. If you `git add` and `git commit` immediately after editing a Kotlin file, the commit can capture the **unformatted** version while format finishes after — the pre-push hook (`./check`) silently re-formats and passes, but the commit on the branch is still stale and CI ktlint fails.

Before committing Kotlin changes, run `./format` synchronously yourself (or wait for the async run to finish, then `git diff` to confirm a clean working tree) so the staged version is what ktlint will see in CI.

# Markdown Formatting

## Tables
Format markdown tables with aligned column whitespace (use available IDE lint tools to resolve this if possible)
❌ Bad: | Col1 | Col2 |
✅ Good: | Col1   | Col2   |
Table headers and cells should align vertically

The formatting might need to be adjusted as the content in the table changes.

## Links

### Directories
When linking to directories in Markdown, don't include a trailing file separator
❌ Bad: [.docs/](.docs/)
✅ Good: [.docs/](.docs)
Links to directories shouldn't have trailing file separators

### Paths in Markdown links
Markdown links should ALWAYS use absolute paths, where '/' refers to the project root, and never use relative paths
❌ Bad: [.docs/](../../../.docs)
✅ Good: [.docs/](/.docs)
Markdown links should ALWAYS use absolute paths where '/' refers to the project root

### Documentation Reference
For complete patterns: .docs/workflow/quality.md
