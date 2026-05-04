package com.eygraber.jellyfin.ui.material.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * A themed wrapper around Coil's image loading that:
 *  - Tints the loading/empty background with the Material theme's surface variant.
 *  - Renders [fallback] while the image is loading, on error, or when [model] is null.
 *  - Renders the loaded image with [contentScale] once it succeeds.
 *
 * @param model The image URL (or any Coil-supported model). When null the [fallback] is shown.
 * @param contentDescription Accessibility description of the image, or null if purely decorative.
 * @param modifier Modifier to apply to the outer container.
 * @param contentScale How the image content should be scaled inside its bounds.
 * @param fallback Composable rendered while loading, on error, or when [model] is null.
 */
@Composable
fun JellyfinAsyncImage(
  model: Any?,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Crop,
  fallback: @Composable () -> Unit = {},
) {
  val painter = rememberAsyncImagePainter(
    model = ImageRequest.Builder(LocalPlatformContext.current)
      .data(model)
      .crossfade(enable = true)
      .build(),
  )
  val state by painter.state.collectAsState()
  val isImageLoaded = model != null && state is AsyncImagePainter.State.Success

  Box(
    modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
    contentAlignment = Alignment.Center,
  ) {
    if(isImageLoaded) {
      Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = Modifier.fillMaxSize(),
      )
    }
    else {
      fallback()
    }
  }
}
