package com.eygraber.jellyfin.nav

import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.screens.home.HomeKey
import com.eygraber.jellyfin.screens.library.movies.MoviesLibraryKey
import com.eygraber.jellyfin.screens.library.tvshows.TvShowsLibraryKey
import com.eygraber.jellyfin.screens.root.RootKey
import com.eygraber.jellyfin.screens.tvshow.episodes.TvShowEpisodesKey
import com.eygraber.jellyfin.screens.tvshow.seasons.TvShowSeasonsKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

internal fun PolymorphicModuleBuilder<NavKey>.addSubclasses() {
  subclass(HomeKey::class, HomeKey.serializer())
  subclass(MoviesLibraryKey::class, MoviesLibraryKey.serializer())
  subclass(RootKey::class, RootKey.serializer())
  subclass(TvShowEpisodesKey::class, TvShowEpisodesKey.serializer())
  subclass(TvShowSeasonsKey::class, TvShowSeasonsKey.serializer())
  subclass(TvShowsLibraryKey::class, TvShowsLibraryKey.serializer())
}
