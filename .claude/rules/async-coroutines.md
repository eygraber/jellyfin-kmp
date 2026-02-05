---
paths:
  - "**/*.kt"
---

Use Kotlin Coroutines and Flow for asynchronous operations
Always use injectable dispatchers (JellyfinDispatchers), never hardcoded Dispatchers
UI scopes should come from Compose
For non-UI work, inject AppCoroutineScope
Properly manage coroutine lifecycles to avoid memory leaks
Use Flow for reactive data streams from repositories and data sources
Launch coroutines with appropriate scope based on lifecycle needs
Make sure CancellationException isn't swallowed, use runCatchingCoroutine

### Documentation Reference
For complete patterns: .docs/data/
