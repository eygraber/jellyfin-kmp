package com.eygraber.jellyfin.ui.material.text

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TextFieldState.UpdateEffect(
  onUpdate: (CharSequence) -> Unit,
) {
  LaunchedEffect(this, onUpdate) {
    snapshotFlow { text }.collect { name ->
      onUpdate(name)
    }
  }
}

@Composable
fun TextFieldState.UpdateEffectLatest(
  onUpdate: suspend (CharSequence) -> Unit,
) {
  LaunchedEffect(this, onUpdate) {
    snapshotFlow { text }.collectLatest { name ->
      onUpdate(name)
    }
  }
}

@Composable
inline fun <T> TextFieldState.UpdateEffect(
  noinline onUpdate: (T) -> Unit,
  crossinline transform: Flow<CharSequence>.() -> Flow<T>,
) {
  LaunchedEffect(this, onUpdate) {
    snapshotFlow { text }.transform().collect { name ->
      onUpdate(name)
    }
  }
}
