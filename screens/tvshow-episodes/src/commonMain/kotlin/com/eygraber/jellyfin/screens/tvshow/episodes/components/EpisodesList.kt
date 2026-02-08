package com.eygraber.jellyfin.screens.tvshow.episodes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.tvshow.episodes.EpisodeItem

private const val THUMBNAIL_ASPECT_RATIO = 16F / 9F

@Composable
internal fun EpisodesList(
  episodes: List<EpisodeItem>,
  onEpisodeClick: (episodeId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    items(
      items = episodes,
      key = { it.id },
    ) { episode ->
      EpisodeCard(
        episode = episode,
        onClick = { onEpisodeClick(episode.id) },
      )
    }
  }
}

@Composable
private fun EpisodeCard(
  episode: EpisodeItem,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.Top,
    ) {
      Box(
        modifier = Modifier
          .width(episodeThumbnailWidth)
          .aspectRatio(THUMBNAIL_ASPECT_RATIO),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = episode.episodeNumber?.toString() ?: "?",
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(
        modifier = Modifier
          .weight(1F)
          .padding(12.dp),
      ) {
        val title = buildString {
          episode.episodeNumber?.let { append("E$it - ") }
          append(episode.name)
        }
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        episode.runtimeMinutes?.let { minutes ->
          Text(
            text = "${minutes}min",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        episode.overview?.let { overview ->
          Text(
            text = overview,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
          )
        }
      }
    }
  }
}

private val episodeThumbnailWidth = 160.dp
