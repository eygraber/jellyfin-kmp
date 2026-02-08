package com.eygraber.jellyfin.nav

import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.screens.collection.items.CollectionItemsKey
import com.eygraber.jellyfin.screens.episode.detail.EpisodeDetailKey
import com.eygraber.jellyfin.screens.genre.items.GenreItemsKey
import com.eygraber.jellyfin.screens.home.HomeKey
import com.eygraber.jellyfin.screens.library.collections.CollectionsLibraryKey
import com.eygraber.jellyfin.screens.library.genres.GenresLibraryKey
import com.eygraber.jellyfin.screens.library.movies.MoviesLibraryKey
import com.eygraber.jellyfin.screens.library.music.MusicLibraryKey
import com.eygraber.jellyfin.screens.library.tvshows.TvShowsLibraryKey
import com.eygraber.jellyfin.screens.movie.detail.MovieDetailKey
import com.eygraber.jellyfin.screens.music.album.tracks.AlbumTracksKey
import com.eygraber.jellyfin.screens.music.artist.albums.ArtistAlbumsKey
import com.eygraber.jellyfin.screens.root.RootKey
import com.eygraber.jellyfin.screens.search.SearchKey
import com.eygraber.jellyfin.screens.tvshow.detail.TvShowDetailKey
import com.eygraber.jellyfin.screens.tvshow.episodes.TvShowEpisodesKey
import com.eygraber.jellyfin.screens.tvshow.seasons.TvShowSeasonsKey
import com.eygraber.jellyfin.screens.video.player.VideoPlayerKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

internal fun PolymorphicModuleBuilder<NavKey>.addSubclasses() {
  subclass(AlbumTracksKey::class, AlbumTracksKey.serializer())
  subclass(ArtistAlbumsKey::class, ArtistAlbumsKey.serializer())
  subclass(CollectionItemsKey::class, CollectionItemsKey.serializer())
  subclass(CollectionsLibraryKey::class, CollectionsLibraryKey.serializer())
  subclass(EpisodeDetailKey::class, EpisodeDetailKey.serializer())
  subclass(GenreItemsKey::class, GenreItemsKey.serializer())
  subclass(GenresLibraryKey::class, GenresLibraryKey.serializer())
  subclass(HomeKey::class, HomeKey.serializer())
  subclass(MovieDetailKey::class, MovieDetailKey.serializer())
  subclass(MoviesLibraryKey::class, MoviesLibraryKey.serializer())
  subclass(MusicLibraryKey::class, MusicLibraryKey.serializer())
  subclass(RootKey::class, RootKey.serializer())
  subclass(SearchKey::class, SearchKey.serializer())
  subclass(TvShowDetailKey::class, TvShowDetailKey.serializer())
  subclass(TvShowEpisodesKey::class, TvShowEpisodesKey.serializer())
  subclass(TvShowSeasonsKey::class, TvShowSeasonsKey.serializer())
  subclass(TvShowsLibraryKey::class, TvShowsLibraryKey.serializer())
  subclass(VideoPlayerKey::class, VideoPlayerKey.serializer())
}
