package com.eygraber.jellyfin.ui.library.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.ui.icons.FilterList
import com.eygraber.jellyfin.ui.icons.JellyfinIcons

/**
 * Filter icon button with badge showing active filter count.
 */
@Composable
fun FilterButton(
  activeFilterCount: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    IconButton(onClick = onClick) {
      Icon(
        imageVector = JellyfinIcons.FilterList,
        contentDescription = "Filter",
      )
    }

    if(activeFilterCount > 0) {
      Badge(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .offset(x = (-4).dp, y = 4.dp)
          .size(16.dp),
      ) {
        Text(activeFilterCount.toString())
      }
    }
  }
}
