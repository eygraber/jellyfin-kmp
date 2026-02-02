# Preview Configuration

## Preview Annotation

**Use `@PreviewTemplateScreen` for screen previews** (if available):

```kotlin
@PreviewTemplateScreen
@Composable
private fun MyScreenPreview(
  @PreviewParameter(MyPreviewProvider::class)
  state: MyState,
) {
  TemplateEdgeToEdgePreviewTheme {
    MyScreen(
      onDismissError = {},
      onNavigateBack = {},
      state = state,
    )
  }
}
```

This provides multiple preview variants (light/dark, font scales, locales).

## Preview Visibility

**Preview composables must be `private`**:

```kotlin
// Good
@Preview
@Composable
private fun MyScreenPreview() { }

// Bad
@Preview
@Composable
fun MyScreenPreview() { }
```

## Preview Parameters

**Use `PreviewParameterProvider` for multiple states**:

```kotlin
class MyPreviewProvider : PreviewParameterProvider<MyState> {
  override val values = sequenceOf(
    MyState(/* default state */),
    MyState(/* loading state */),
    MyState(/* error state */),
  )
}

@Preview
@Composable
private fun Preview(
  @PreviewParameter(MyPreviewProvider::class)
  state: MyState,
) { }
```

Prefer to have a default instance of the state, and copy it for other variations:

```kotlin
private val state = MyState(
  action = "Next",
  errorDialogTitle = "Error",
  hint = "Enter value",
  isLoading = false,
)

private val loadingState = state.copy(
  isLoading = true,
)
```

## Common Preview States

Cover these states in previews:
- Default/empty
- Loading
- With data
- Error
- Edge cases (long text, empty lists)

## Previews for Extracted Composables

**When extracting composables to separate files, strongly consider creating previews**:

```kotlin
// compose/ProductListItem.kt
@Composable
internal fun ProductListItem(
  onAddToCart: () -> Unit,
  name: String,
  price: String,
  isInStock: Boolean,
  modifier: Modifier = Modifier,
) {
  // Implementation
}

@Preview
@Composable
private fun ProductListItemPreview(
  @PreviewParameter(ProductListItemPreviewProvider::class)
  params: ProductListItemParams,
) {
  TemplatePreviewTheme {
    ProductListItem(
      onAddToCart = {},
      name = params.name,
      price = params.price,
      isInStock = params.isInStock,
    )
  }
}
```

### Prefer Single Preview with PreviewParameterProvider

**Use `PreviewParameterProvider` to show multiple states in one preview function**:

```kotlin
private data class ProductListItemParams(
  val name: String,
  val price: String,
  val isInStock: Boolean,
)

private class ProductListItemPreviewProvider : PreviewParameterProvider<ProductListItemParams> {
  override val values = sequenceOf(
    ProductListItemParams(
      name = "Product Name",
      price = "$19.99",
      isInStock = true,
    ),
    ProductListItemParams(
      name = "Product Name",
      price = "$19.99",
      isInStock = false,
    ),
    ProductListItemParams(
      name = "Very Long Product Name That Might Wrap to Multiple Lines",
      price = "$999.99",
      isInStock = true,
    ),
  )
}
```

This creates separate preview variants for each state, making it easy to visualize all cases.

### Multiple Preview Functions

**Only use multiple preview functions when PreviewParameterProvider doesn't make sense**:

```kotlin
// Acceptable - very different compositions that can't share parameters easily
@Preview
@Composable
private fun EmptyProductListPreview() {
  TemplatePreviewTheme {
    ProductList(
      products = emptyList(),
      onRefresh = {},
    )
  }
}

@Preview
@Composable
private fun LoadedProductListPreview() {
  TemplatePreviewTheme {
    ProductList(
      products = sampleProducts,
      onRefresh = {},
    )
  }
}
```

Use this approach when:
- The composable states require entirely different parameter structures
- One state needs special setup (e.g., empty state vs. loaded state)
- PreviewParameterProvider would be more complex than multiple functions

### Default Preference

**Skew towards having one preview function** with PreviewParameterProvider unless there's a good reason
not to. This keeps preview code organized and makes it easy to add new state variations.
