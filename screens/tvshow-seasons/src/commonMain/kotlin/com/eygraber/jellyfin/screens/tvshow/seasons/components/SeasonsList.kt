package com.eygraber.jellyfin.screens.tvshow.seasons.components

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
import com.eygraber.jellyfin.screens.tvshow.seasons.SeasonItem

private const val POSTER_ASPECT_RATIO = 2F / 3F

@Composable
internal fun SeasonsList(
  seasons: List<SeasonItem>,
  onSeasonClick: (seasonId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    items(
      items = seasons,
      key = { it.id },
    ) { season ->
      SeasonCard(
        season = season,
        onClick = { onSeasonClick(season.id) },
      )
    }
  }
}

@Composable
private fun SeasonCard(
  season: SeasonItem,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier
          .width(seasonPosterWidth)
          .aspectRatio(POSTER_ASPECT_RATIO),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = season.seasonNumber?.toString() ?: "?",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(
        modifier = Modifier
          .weight(1F)
          .padding(12.dp),
      ) {
        Text(
          text = season.name,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        season.episodeCount?.let { count ->
          Text(
            text = "$count episode${if(count != 1) "s" else ""}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

private val seasonPosterWidth = 100.dp
