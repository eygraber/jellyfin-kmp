package com.eygraber.jellyfin.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
internal fun ContinueWatchingLoading(
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Text(
      text = "Continue Watching",
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(horizontal = 16.dp),
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      userScrollEnabled = false,
    ) {
      items(PlaceholderCount) {
        Card(
          modifier = Modifier.width(placeholderCardWidth),
        ) {
          Column {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(placeholderImageHeight)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            )

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(placeholderProgressHeight)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            )

            Column(
              modifier = Modifier.padding(8.dp),
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(PlaceholderTextWidthFraction)
                  .height(placeholderTextHeight)
                  .clip(MaterialTheme.shapes.small)
                  .background(MaterialTheme.colorScheme.surfaceVariant),
              )
            }
          }
        }
      }
    }
  }
}

private const val PlaceholderCount = 4
private val placeholderCardWidth = 160.dp
private val placeholderImageHeight = 90.dp
private val placeholderProgressHeight = 4.dp
private val placeholderTextHeight = 12.dp
private const val PlaceholderTextWidthFraction = 0.7F
