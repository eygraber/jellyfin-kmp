---
paths:
  - "**/*.kt"
---

# Formatting and Style
Use 2-space indentation for all Kotlin files (not 4 spaces)
Follow ktlint formatting - runs automatically via ./format script (can be run often, only takes a few seconds to run)
Follow detekt rules as configured in detekt.yml - runs automatically via ./detekt script (don't run often, it takes time to run)
Maximum line length of 120 characters for all files
Keep imports organized and remove unused imports
Don't use FQNs in code; prefer imports and import aliases
After ktlint and detekt, follow Kotlin coding conventions from https://kotlinlang.org/docs/coding-conventions.html
When ktlint and IDE formatting disagree, ktlint wins

When importing an R class (for Android resources) that is from a different module, always use an appropriately named import alias
Bad: R.string.other_feature_label
Good: OtherFeatureR.string.other_feature_label

# Immutability and Safety
Use val over var whenever possible for immutability
Bad: var count = 0
Good: val count = 0

Prefer immutable collections (List, Map, Set) over mutable alternatives
Bad: val list = mutableListOf<String>()
Good: val list = listOf<String>()

Leverage Kotlin's null safety features effectively
Bad: val name: String? = user!!.name
Good: val name: String? = user?.name

# Code Organization
Avoid labeled expressions unless absolutely necessary
Avoid: items.forEach loop@{ if (it.invalid) return@loop }
Prefer: items.filter { it.valid }.forEach { process(it) }

### Documentation Reference
For complete patterns: .docs/workflow/quality.md
