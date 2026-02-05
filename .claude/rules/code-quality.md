Run ./check to execute all PR checks
Use ktlint for code formatting (configured via .editorconfig)
Run Android lint on app module for performance: ./gradlew :apps:android:lintDevRelease
Use Detekt for static analysis (both with and without type resolution)
Never modify detekt.yml files without permission
detekt.yml files can be read to provide context on why a violation may be occurring
Use Dependency Analysis Plugin to verify correct dependency configurations
Use Konsist tests to enforce architectural rules
Custom lint checks are in jellyfin-lint included build
Format violations should be fixed automatically with ./format script
Detekt runs in two modes: no type resolution and with type resolution
Pass --continue to detekt to report all issues instead of stopping early
IDE formatting rules are in .idea/codeStyles (mostly compatible with ktlint)
When ktlint and IDE disagree, ktlint wins

# Markdown Formatting

## Tables
Format markdown tables with aligned column whitespace (use available IDE lint tools to resolve this if possible)
Bad: | Col1 | Col2 |
Good: | Col1   | Col2   |
Table headers and cells should align vertically

The formatting might need to be adjusted as the content in the table changes.

## Links

### Directories
When linking to directories in Markdown, don't include a trailing file separator
Bad: [.docs/](.docs/)
Good: [.docs/](.docs)
Links to directories shouldn't have trailing file separators

### Paths in Markdown links
Markdown links should ALWAYS use absolute paths, where '/' refers to the project root, and never use relative paths
Bad: [.docs/](../../../.docs)
Good: [.docs/](/.docs)
Markdown links should ALWAYS use absolute paths where '/' refers to the project root

### Documentation Reference
For complete patterns: .docs/workflow/quality.md
