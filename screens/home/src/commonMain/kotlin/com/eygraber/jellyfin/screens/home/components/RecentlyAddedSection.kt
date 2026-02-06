package com.eygraber.jellyfin.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.home.RecentlyAddedItem

@Composable
internal fun RecentlyAddedSection(
  items: List<RecentlyAddedItem>,
  onItemClick: (itemId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Text(
      text = "Recently Added",
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(horizontal = 16.dp),
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      items(
        items = items,
        key = { it.id },
      ) { item ->
        RecentlyAddedCard(
          item = item,
          onClick = { onItemClick(item.id) },
        )
      }
    }
  }
}

@Composable
private fun RecentlyAddedCard(
  item: RecentlyAddedItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onClick,
    modifier = modifier.width(recentlyAddedCardWidth),
  ) {
    Column {
      Box(
        modifier = Modifier
          .width(recentlyAddedCardWidth)
          .height(recentlyAddedImageHeight)
          .clip(MaterialTheme.shapes.medium)
          .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = item.name.take(1).uppercase(),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(
        modifier = Modifier.padding(8.dp),
      ) {
        Text(
          text = item.seriesName ?: item.name,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        val yearText = item.productionYear?.toString()
        if(yearText != null) {
          Text(
            text = yearText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

private val recentlyAddedCardWidth = 120.dp
private val recentlyAddedImageHeight = 180.dp
