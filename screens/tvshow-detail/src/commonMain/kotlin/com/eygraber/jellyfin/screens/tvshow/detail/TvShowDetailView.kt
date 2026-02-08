package com.eygraber.jellyfin.screens.tvshow.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.Star
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView
import kotlin.math.roundToInt

internal typealias TvShowDetailView = ViceView<TvShowDetailIntent, TvShowDetailViewState>

private const val BACKDROP_ASPECT_RATIO = 16F / 9F
private const val SEASON_POSTER_ASPECT_RATIO = 2F / 3F
private const val RATING_DECIMAL_FACTOR = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TvShowDetailView(
  state: TvShowDetailViewState,
  onIntent: (TvShowDetailIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {},
          navigationIcon = {
            IconButton(onClick = { onIntent(TvShowDetailIntent.NavigateBack) }) {
              Icon(
                imageVector = JellyfinIcons.ArrowBack,
                contentDescription = "Navigate back",
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
          ),
        )
      },
    ) { contentPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding),
      ) {
        when {
          state.isLoading -> LoadingContent()
          state.error != null -> ErrorContent(
            error = state.error,
            onRetry = { onIntent(TvShowDetailIntent.RetryLoad) },
          )
          state.show != null -> ShowContent(
            show = state.show,
            seasons = state.seasons,
            onSeasonClick = { seasonId -> onIntent(TvShowDetailIntent.SelectSeason(seasonId)) },
          )
        }
      }
    }
  }
}

@Composable
private fun ShowContent(
  show: TvShowDetail,
  seasons: List<TvShowSeasonSummary>,
  onSeasonClick: (seasonId: String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
  ) {
    BackdropSection(show = show)

    Column(
      modifier = Modifier.padding(horizontal = 16.dp),
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = show.name,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
      )

      Spacer(modifier = Modifier.height(8.dp))

      MetadataRow(show = show)

      show.overview?.let { overview ->
        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Overview",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = overview,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    if(seasons.isNotEmpty()) {
      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Seasons",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp),
      )

      Spacer(modifier = Modifier.height(8.dp))

      SeasonsRow(
        seasons = seasons,
        onSeasonClick = onSeasonClick,
      )
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
private fun BackdropSection(
  show: TvShowDetail,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(BACKDROP_ASPECT_RATIO),
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surfaceVariant),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = show.name.take(1),
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            ),
            startY = Float.POSITIVE_INFINITY / 2,
          ),
        ),
    )
  }
}

@Composable
private fun MetadataRow(
  show: TvShowDetail,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    show.productionYear?.let { year ->
      Text(
        text = year.toString(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    if(show.seasonCount > 0) {
      Text(
        text = "${show.seasonCount} Season${if(show.seasonCount != 1) "s" else ""}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    show.officialRating?.let { rating ->
      Text(
        text = rating,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }

    show.communityRating?.let { rating ->
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Icon(
          imageVector = JellyfinIcons.Star,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
          tint = MaterialTheme.colorScheme.primary,
        )
        Text(
          text = rating.formatRating(),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }
  }
}

@Composable
private fun SeasonsRow(
  seasons: List<TvShowSeasonSummary>,
  onSeasonClick: (seasonId: String) -> Unit,
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
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
  season: TvShowSeasonSummary,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier
      .width(120.dp)
      .clickable(onClick = onClick),
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(SEASON_POSTER_ASPECT_RATIO),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = season.name.take(2),
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(
        modifier = Modifier.padding(8.dp),
      ) {
        Text(
          text = season.name,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        season.episodeCount?.let { count ->
          Text(
            text = "$count episode${if(count != 1) "s" else ""}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

private fun Float.formatRating(): String {
  val rounded = (this * RATING_DECIMAL_FACTOR).roundToInt()
  return "${rounded / RATING_DECIMAL_FACTOR}.${rounded % RATING_DECIMAL_FACTOR}"
}

@Composable
private fun LoadingContent() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}

@Composable
private fun ErrorContent(
  error: TvShowDetailError,
  onRetry: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = error.message,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.error,
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = onRetry) {
      Text("Retry")
    }
  }
}

@PreviewJellyfinScreen
@Composable
private fun TvShowDetailLoadingPreview() {
  JellyfinPreviewTheme {
    TvShowDetailView(
      state = TvShowDetailViewState.Loading,
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun TvShowDetailErrorPreview() {
  JellyfinPreviewTheme {
    TvShowDetailView(
      state = TvShowDetailViewState(
        isLoading = false,
        error = TvShowDetailError.Network(),
      ),
      onIntent = {},
    )
  }
}

@Suppress("MagicNumber")
@PreviewJellyfinScreen
@Composable
private fun TvShowDetailContentPreview() {
  JellyfinPreviewTheme {
    TvShowDetailView(
      state = TvShowDetailViewState(
        isLoading = false,
        show = TvShowDetail(
          id = "1",
          name = "Breaking Bad",
          overview = "A chemistry teacher diagnosed with inoperable lung cancer turns to " +
            "manufacturing and selling methamphetamine with a former student.",
          productionYear = 2008,
          communityRating = 9.5F,
          officialRating = "TV-MA",
          seasonCount = 5,
          backdropImageUrl = null,
          posterImageUrl = null,
        ),
        seasons = listOf(
          TvShowSeasonSummary(
            id = "s1",
            name = "Season 1",
            seasonNumber = 1,
            episodeCount = 7,
            imageUrl = null,
          ),
          TvShowSeasonSummary(
            id = "s2",
            name = "Season 2",
            seasonNumber = 2,
            episodeCount = 13,
            imageUrl = null,
          ),
          TvShowSeasonSummary(
            id = "s3",
            name = "Season 3",
            seasonNumber = 3,
            episodeCount = 13,
            imageUrl = null,
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
