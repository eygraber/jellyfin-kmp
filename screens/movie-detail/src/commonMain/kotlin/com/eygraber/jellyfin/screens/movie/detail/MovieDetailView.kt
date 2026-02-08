package com.eygraber.jellyfin.screens.movie.detail

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.Person
import com.eygraber.jellyfin.ui.icons.PlayArrow
import com.eygraber.jellyfin.ui.icons.Star
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView
import kotlin.math.roundToInt

internal typealias MovieDetailView = ViceView<MovieDetailIntent, MovieDetailViewState>

private const val BACKDROP_ASPECT_RATIO = 16F / 9F
private const val SIMILAR_POSTER_ASPECT_RATIO = 2F / 3F
private const val PERSON_IMAGE_SIZE = 80
private const val RATING_DECIMAL_FACTOR = 10
private const val PLAY_ICON_SIZE = 18

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovieDetailView(
  state: MovieDetailViewState,
  onIntent: (MovieDetailIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {},
          navigationIcon = {
            IconButton(onClick = { onIntent(MovieDetailIntent.NavigateBack) }) {
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
            onRetry = { onIntent(MovieDetailIntent.RetryLoad) },
          )
          state.movie != null -> MovieContent(
            movie = state.movie,
            cast = state.cast,
            crew = state.crew,
            similarItems = state.similarItems,
            onSimilarItemClick = { itemId ->
              onIntent(MovieDetailIntent.SelectSimilarItem(itemId))
            },
          )
        }
      }
    }
  }
}

@Composable
private fun MovieContent(
  movie: MovieDetail,
  cast: List<CastMember>,
  crew: List<CrewMember>,
  similarItems: List<SimilarItem>,
  onSimilarItemClick: (itemId: String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
  ) {
    BackdropSection(movie = movie)

    Column(
      modifier = Modifier.padding(horizontal = 16.dp),
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = movie.name,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
      )

      Spacer(modifier = Modifier.height(8.dp))

      MetadataRow(movie = movie)

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = { },
      ) {
        Icon(
          imageVector = JellyfinIcons.PlayArrow,
          contentDescription = null,
          modifier = Modifier.size(PLAY_ICON_SIZE.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Play")
      }

      movie.overview?.let { overview ->
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

    if(cast.isNotEmpty()) {
      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Cast",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp),
      )

      Spacer(modifier = Modifier.height(8.dp))

      CastRow(cast = cast)
    }

    if(crew.isNotEmpty()) {
      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Crew",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp),
      )

      Spacer(modifier = Modifier.height(8.dp))

      CrewRow(crew = crew)
    }

    if(similarItems.isNotEmpty()) {
      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "More Like This",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp),
      )

      Spacer(modifier = Modifier.height(8.dp))

      SimilarItemsRow(
        items = similarItems,
        onItemClick = onSimilarItemClick,
      )
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
private fun BackdropSection(
  movie: MovieDetail,
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
        text = movie.name.take(1),
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
  movie: MovieDetail,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    movie.productionYear?.let { year ->
      Text(
        text = year.toString(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    movie.runtimeMinutes?.let { minutes ->
      Text(
        text = formatRuntime(minutes),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    movie.officialRating?.let { rating ->
      Text(
        text = rating,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }

    movie.communityRating?.let { rating ->
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

private fun formatRuntime(minutes: Int): String {
  val hours = minutes / 60
  val remainingMinutes = minutes % 60
  return if(hours > 0) {
    "${hours}h ${remainingMinutes}m"
  }
  else {
    "${remainingMinutes}m"
  }
}

@Composable
private fun CastRow(
  cast: List<CastMember>,
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    items(
      items = cast,
      key = { it.id },
    ) { member ->
      PersonCard(
        name = member.name,
        subtitle = member.role,
      )
    }
  }
}

@Composable
private fun CrewRow(
  crew: List<CrewMember>,
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    items(
      items = crew,
      key = { it.id },
    ) { member ->
      PersonCard(
        name = member.name,
        subtitle = member.job,
      )
    }
  }
}

@Composable
private fun PersonCard(
  name: String,
  subtitle: String?,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.width(PERSON_IMAGE_SIZE.dp),
  ) {
    Box(
      modifier = Modifier
        .size(PERSON_IMAGE_SIZE.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = JellyfinIcons.Person,
        contentDescription = null,
        modifier = Modifier.size((PERSON_IMAGE_SIZE / 2).dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = name,
      style = MaterialTheme.typography.labelSmall,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center,
    )

    subtitle?.let { subtitleText ->
      Text(
        text = subtitleText,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
private fun SimilarItemsRow(
  items: List<SimilarItem>,
  onItemClick: (itemId: String) -> Unit,
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    items(
      items = items,
      key = { it.id },
    ) { item ->
      SimilarItemCard(
        item = item,
        onClick = { onItemClick(item.id) },
      )
    }
  }
}

@Composable
private fun SimilarItemCard(
  item: SimilarItem,
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
          .aspectRatio(SIMILAR_POSTER_ASPECT_RATIO),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = item.name.take(1),
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(
        modifier = Modifier.padding(8.dp),
      ) {
        Text(
          text = item.name,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        item.productionYear?.let { year ->
          Text(
            text = year.toString(),
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
  error: MovieDetailError,
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
private fun MovieDetailLoadingPreview() {
  JellyfinPreviewTheme {
    MovieDetailView(
      state = MovieDetailViewState.Loading,
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun MovieDetailErrorPreview() {
  JellyfinPreviewTheme {
    MovieDetailView(
      state = MovieDetailViewState(
        isLoading = false,
        error = MovieDetailError.Network(),
      ),
      onIntent = {},
    )
  }
}

@Suppress("MagicNumber", "LongMethod")
@PreviewJellyfinScreen
@Composable
private fun MovieDetailContentPreview() {
  JellyfinPreviewTheme {
    MovieDetailView(
      state = MovieDetailViewState(
        isLoading = false,
        movie = MovieDetail(
          id = "1",
          name = "Inception",
          overview = "A thief who steals corporate secrets through the use of dream-sharing " +
            "technology is given the inverse task of planting an idea into the mind of a C.E.O.",
          productionYear = 2010,
          communityRating = 8.8F,
          officialRating = "PG-13",
          runtimeMinutes = 148,
          backdropImageUrl = null,
          posterImageUrl = null,
        ),
        cast = listOf(
          CastMember(id = "c1", name = "Leonardo DiCaprio", role = "Cobb", imageUrl = null),
          CastMember(id = "c2", name = "Joseph Gordon-Levitt", role = "Arthur", imageUrl = null),
          CastMember(id = "c3", name = "Elliot Page", role = "Ariadne", imageUrl = null),
        ),
        crew = listOf(
          CrewMember(id = "cr1", name = "Christopher Nolan", job = "Director", imageUrl = null),
          CrewMember(id = "cr2", name = "Hans Zimmer", job = "Music", imageUrl = null),
        ),
        similarItems = listOf(
          SimilarItem(id = "s1", name = "Interstellar", productionYear = 2014, imageUrl = null),
          SimilarItem(id = "s2", name = "The Matrix", productionYear = 1999, imageUrl = null),
        ),
      ),
      onIntent = {},
    )
  }
}
