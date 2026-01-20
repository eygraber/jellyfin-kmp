package template.nav

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

internal inline fun NavBackStack<NavKey>.push(
  key: NavKey,
  prePush: NavBackStack<NavKey>.() -> Unit = {},
) {
  prePush()
  add(key)
}

internal fun NavBackStack<NavKey>.replaceWith(vararg keys: NavKey) {
  clear()
  addAll(keys)
}

internal inline fun <reified T : NavKey> NavBackStack<NavKey>.pushOnto(
  key: NavKey,
  replace: Boolean,
) {
  push(key) {
    popUntil<T>(
      inclusive = replace,
    )
  }
}

internal fun NavBackStack<NavKey>.pop() = removeLastOrNull()

internal inline fun <reified T : NavKey> NavBackStack<NavKey>.popUntil(
  onRemove: (NavKey) -> Unit = {},
  inclusive: Boolean,
) {
  popUntil(
    onRemove = onRemove,
    inclusive = inclusive,
    predicate = { it is T },
  )
}

internal inline fun NavBackStack<NavKey>.popUntil(
  onRemove: (NavKey) -> Unit = {},
  inclusive: Boolean,
  predicate: (NavKey) -> Boolean,
) {
  var last = lastOrNull()
  while(last?.let(predicate) == false) {
    removeLastOrNull()
    onRemove(last)
    last = lastOrNull()
  }

  if(last != null && inclusive) {
    removeLastOrNull()
  }
}
