# State Management

## Remember

**Use `remember` for state that survives recomposition**:

```kotlin
// Good
@Composable
fun Counter() {
  var count by remember { mutableStateOf(0) }
  Button(onClick = { count++ }) {
    Text("Count: $count")
  }
}

// Bad - resets on recomposition
@Composable
fun Counter() {
  var count by mutableStateOf(0)  // Will reset!
  Button(onClick = { count++ }) {
    Text("Count: $count")
  }
}
```

## Mutable Parameters

**Never pass mutable state types as parameters**:

```kotlin
// Bad
@Composable
fun UserList(users: MutableState<List<User>>) { }

// Good
@Composable
fun UserList(users: List<User>) { }
```

Mutable parameters break Compose's optimization and make recomposition unpredictable.

## State Hoisting

**Hoist state to the lowest common ancestor**:

```kotlin
// Good - stateless, reusable
@Composable
fun TextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) { }

// Bad - internal state reduces flexibility
@Composable
fun TextField(modifier: Modifier = Modifier) {
  var value by remember { mutableStateOf("") }
}
```

Benefits:
- More reusable
- Easier to test
- Single source of truth

## Immutability

**Use immutable data classes for state**:

```kotlin
// Good
data class UserState(
  val name: String,
  val email: String,
)

// Bad - mutable properties
data class UserState(
  var name: String,
  var email: String,
)
```

Immutable state helps Compose optimize recomposition.

## Surviving Composition Removal

### rememberSaveable for Simple Types

**Use `rememberSaveable` when no specialized type exists**:

```kotlin
// Good - survives process death
@Composable
fun TabScreen() {
  var selectedTab by rememberSaveable { mutableIntStateOf(0) }
  TabRow(selectedTabIndex = selectedTab) {
    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 })
    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 })
  }
}

// Bad - lost on process death
@Composable
fun TabScreen() {
  var selectedTab by remember { mutableIntStateOf(0) }
  // Tab selection lost!
}
```

`rememberSaveable` works automatically with:
- Primitives (Int, String, Boolean, etc.)
- Parcelable types
- Serializable types
- Types in a Bundle
- Types that have a custom `Saver`

### Prefer Specialized Types

**Many Compose types already handle state survival internally**. Use them when available:

```kotlin
// Good - TextFieldState uses rememberSaveable internally
@Composable
fun SearchScreen() {
  val searchQuery = rememberTextFieldState()
  TextField(state = searchQuery)
}

// Bad - manually managing text state
@Composable
fun SearchScreen() {
  var searchQuery by rememberSaveable { mutableStateOf("") }
  TextField(
    value = searchQuery,
    onValueChange = { searchQuery = it }
  )
}
```

Examples of types with built-in state survival:
- `TextFieldState` - text input
- `LazyListState` - list scroll position
- `ScaffoldState` - scaffold UI state

### When to Use Each

| Use Case                  | Tool                              | Example                            |
|---------------------------|-----------------------------------|------------------------------------|
| Transient UI state        | `remember`                        | Animation state, dialog visibility |
| Simple UI state           | `rememberSaveable`                | Selected tab, toggle state         |
| Specialized Compose types | Built-in (e.g., `TextFieldState`) | Text input, list state             |
| Model state               | Model's `State`                   | Data from API, business logic      |

### Don't Save Everything

**Only save user-facing state**, not derived or transient values:

```kotlin
// Good - only save user input
@Composable
fun FilterScreen() {
  var selectedFilter by rememberSaveable { mutableStateOf(FilterOptions()) }

  // Derived state - don't save
  val isValid = selectedFilter.startDate <= selectedFilter.endDate

  // Transient UI state - don't save
  var showDatePicker by remember { mutableStateOf(false) }
}

// Bad - saving everything
@Composable
fun FilterScreen() {
  var selectedFilter by rememberSaveable { mutableStateOf(FilterOptions()) }
  var isValid by rememberSaveable { mutableStateOf(true) } // Derived!
  var showDatePicker by rememberSaveable { mutableStateOf(false) } // Transient!
}
```
