---
paths:
  - "**/*Composable*.kt"
  - "**/*View.kt"
  - "**/compose/**/*.kt"
---

# Naming and Structure
Use PascalCase naming for all composable functions
Bad: fun userProfile() {}
Good: fun UserProfile() {}

CompositionLocals must be prefixed with Local (e.g., LocalUserSession)
Bad: val UserSession = compositionLocalOf...
Good: val LocalUserSession = compositionLocalOf...

Break up composables into multiple functions for performance and clarity
Pass only the parameters needed to child composables - don't pass entire ViewState or large objects
Exception: When most parameters are in a stable container object (mark as @Immutable/@Stable)

Small (< 15 lines, not counting the composable's parameters) and tightly coupled child composables should be private in same file
Larger (>= 15 lines) or independently useful child composables should be internal in separate file
Extracted composables in separate files should strongly consider having a preview
Use private visibility for child composables within a screen's View file
Extract large/numerous child composables to a `compose/` package with internal visibility

# Modifiers
Every pubic composable that emits UI must accept a modifier: Modifier = Modifier parameter
`internal`/`private` composables can accept a modifier: Modifier = Modifier parameter if needed
Position modifier as the first optional parameter (after required params, before other optional params)
Never reuse the same modifier instance across multiple composables
Bad: Column(modifier) { Text("A", modifier); Text("B", modifier) }
Good: Column(modifier) { Text("A"); Text("B") }

# State Management
Never pass mutable state types (MutableState, MutableStateFlow) as parameters
Bad: fun UserList(users: MutableState<List<User>>) {}
Good: fun UserList(users: List<User>) {}

Content emitter composables must not return values
Use remember for state that needs to survive recomposition
Hoist state to the appropriate level for reusability
Use immutable data classes for state to help Compose optimize recomposition

State survival: Prefer specialized types (TextFieldState, LazyListState) that handle rememberSaveable internally
For other state that must survive composition removal: use rememberSaveable (primitives) or rememberSerializable (data classes)
Only save user-facing state, not derived values or transient UI state (animations, dialog visibility)

# Architecture Integration (VICE Pattern)
CRITICAL: Never pass onIntent to child composables - use specific callbacks instead
Bad: FirmsLazyColumn(onIntent = onIntent)
Good: FirmsLazyColumn(onSelectFirm = { onIntent(SelectFirm(it)) })

Top-level screen View composable should only accept state and onIntent parameters
Bad: fun MyView(state: State, onIntent: (Intent) -> Unit, title: String)
Good: fun MyView(state: State, onIntent: (Intent) -> Unit)

All screen configuration must be in the ViewState, not as separate parameters
Child composables should use specific, descriptive callback names (onKeyboardAction, onItemClick)
Never inject ViewModels into composables - use VICE pattern instead

# Layout and Composition
Avoid multiple root emitters in a single composable - wrap in Column, Row, or Box
Bad: @Composable fun MyScreen() { Text("A"); Button {} }
Good: @Composable fun MyScreen() { Column { Text("A"); Button {} } }

# Material Design
Use Material 3 components exclusively - Material 2 is forbidden
Bad: import androidx.compose.material.Button
Good: import androidx.compose.material3.Button

# Parameters and Previews
Follow parameter ordering: required lambda params, required params, modifier, optional lambda params, optional params, content slot (last)
Use named arguments on separate lines when calling functions with more than 1 parameter
Named arguments on separate lines may be used when calling a function with 1 parameter
Preview composables should be private and use @Preview annotation
Skew towards having one preview function with PreviewParameterProvider for multiple states
Only use multiple preview functions when PreviewParameterProvider doesn't make sense
Use PreviewParameterProvider for complex preview states
Prefer to have a default instance of the state in the PreviewParameterProvider, and copy it for other variations

# Screen-Specific Composables Package
When composables are too large/numerous to be private in View file:
- Extract to `compose/` package within screen module
- Use internal visibility (not private) to expose the top level composables from these files
- Keep in screen module (not ui/ modules)
- Group files in the `compose/` package by functionality

# When to Move Code to UI Modules
CRITICAL: Only move composables to `ui/` modules when ACTUALLY shared across multiple modules
Bad: Moving to ui/ because it "might be reused later"
Good: Moving to ui/ because 2+ screen modules are currently using it

Exceptions for utilities:
- Generic utilities belong in `:ui:compose` (e.g., layout helpers, state utilities)
- Material Design system components belong in `:ui:material` (e.g., theme, colors, typography)

# Complete Example
See .claude/rules/examples/good-compose-view.kt for a complete View implementation

### Documentation Reference
For complete patterns: .docs/compose/
