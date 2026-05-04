package com.eygraber.jellyfin.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.home.ContinueWatchingItem
import com.eygraber.jellyfin.ui.material.image.JellyfinAsyncImage

@Composable
internal fun MediaCardWithProgress(
  item: ContinueWatchingItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onClick,
    modifier = modifier.width(cardWidth),
  ) {
    Column {
      JellyfinAsyncImage(
        model = item.imageUrl,
        contentDescription = item.name,
        modifier = Modifier
          .fillMaxWidth()
          .height(cardImageHeight)
          .clip(MaterialTheme.shapes.medium),
        fallback = {
          Text(
            text = item.name.take(1).uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        },
      )

      LinearProgressIndicator(
        progress = { item.progressPercent },
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
      )

      Column(
        modifier = Modifier.padding(8.dp),
      ) {
        Text(
          text = item.displayName,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        val subtitle = item.subtitle
        if(subtitle != null) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }
    }
  }
}

private val cardWidth = 160.dp
private val cardImageHeight = 90.dp
