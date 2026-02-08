package com.eygraber.jellyfin.nav

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.screens.home.CollectionType
import com.eygraber.jellyfin.screens.home.HomeNavigator
import com.eygraber.jellyfin.screens.library.movies.MoviesLibraryKey
import com.eygraber.jellyfin.screens.library.movies.MoviesLibraryNavigator
import com.eygraber.jellyfin.screens.library.music.MusicLibraryKey
import com.eygraber.jellyfin.screens.library.music.MusicLibraryNavigator
import com.eygraber.jellyfin.screens.library.tvshows.TvShowsLibraryKey
import com.eygraber.jellyfin.screens.library.tvshows.TvShowsLibraryNavigator
import com.eygraber.jellyfin.screens.music.album.tracks.AlbumTracksKey
import com.eygraber.jellyfin.screens.music.album.tracks.AlbumTracksNavigator
import com.eygraber.jellyfin.screens.music.artist.albums.ArtistAlbumsKey
import com.eygraber.jellyfin.screens.music.artist.albums.ArtistAlbumsNavigator
import com.eygraber.jellyfin.screens.root.RootNavigator
import com.eygraber.jellyfin.screens.tvshow.episodes.TvShowEpisodesKey
import com.eygraber.jellyfin.screens.tvshow.episodes.TvShowEpisodesNavigator
import com.eygraber.jellyfin.screens.tvshow.seasons.TvShowSeasonsKey
import com.eygraber.jellyfin.screens.tvshow.seasons.TvShowSeasonsNavigator
import com.eygraber.jellyfin.screens.welcome.WelcomeKey
import com.eygraber.jellyfin.screens.welcome.WelcomeNavigator

internal object JellyfinNavigators {
  fun root(
    backStack: NavBackStack<NavKey>,
  ) = RootNavigator(
    onNavigateToOnboarding = {
      backStack.replaceWith(WelcomeKey)
    },
  )

  fun welcome(
    backStack: NavBackStack<NavKey>,
  ) = WelcomeNavigator(
    onNavigateToSignUp = { backStack.add(JellyfinNavKeys.ComingSoon("SignUp")) },
    onNavigateToLogin = { backStack.add(JellyfinNavKeys.ComingSoon("Login")) },
  )

  fun home(
    backStack: NavBackStack<NavKey>,
  ) = HomeNavigator(
    onNavigateBack = { backStack.removeLastOrNull() },
    onNavigateToItemDetail = { itemId ->
      backStack.add(JellyfinNavKeys.ComingSoon("Item Detail ($itemId)"))
    },
    onNavigateToLibrary = { libraryId, collectionType ->
      when(collectionType) {
        CollectionType.Movies -> backStack.add(MoviesLibraryKey(libraryId = libraryId))

        CollectionType.TvShows -> backStack.add(TvShowsLibraryKey(libraryId = libraryId))

        CollectionType.Music -> backStack.add(MusicLibraryKey(libraryId = libraryId))

        CollectionType.MusicVideos,
        CollectionType.Collections,
        CollectionType.Playlists,
        CollectionType.LiveTv,
        CollectionType.Photos,
        CollectionType.HomeVideos,
        CollectionType.Books,
        CollectionType.Unknown,
        -> backStack.add(JellyfinNavKeys.ComingSoon("Library ($libraryId)"))
      }
    },
  )

  fun moviesLibrary(
    backStack: NavBackStack<NavKey>,
  ) = MoviesLibraryNavigator(
    onNavigateBack = { backStack.removeLastOrNull() },
    onNavigateToMovieDetail = { movieId ->
      backStack.add(JellyfinNavKeys.ComingSoon("Movie Detail ($movieId)"))
    },
  )

  fun tvShowsLibrary(
    backStack: NavBackStack<NavKey>,
  ) = TvShowsLibraryNavigator(
    onNavigateBack = { backStack.removeLastOrNull() },
    onNavigateToShowSeasons = { showId ->
      backStack.add(TvShowSeasonsKey(seriesId = showId))
    },
  )

  fun tvShowSeasons(
    backStack: NavBackStack<NavKey>,
  ) = TvShowSeasonsNavigator(
    onNavigateBack = { backStack.removeLastOrNull() },
    onNavigateToSeasonEpisodes = { seriesId, seasonId ->
      backStack.add(TvShowEpisodesKey(seriesId = seriesId, seasonId = seasonId))
    },
  )

  fun tvShowEpisodes(
    backStack: NavBackStack<NavKey>,
  ) = TvShowEpisodesNavigator(
    onNavigateBack = { backStack.removeLastOrNull() },
    onNavigateToEpisodeDetail = { episodeId ->
      backStack.add(JellyfinNavKeys.ComingSoon("Episode Detail ($episodeId)"))
    },
  )

  fun musicLibrary(
    backStack: NavBackStack<NavKey>,
  ) = MusicLibraryNavigator(
    onNavigateBack = { backStack.removeLastOrNull() },
    onNavigateToArtistAlbums = { artistId ->
      backStack.add(ArtistAlbumsKey(artistId = artistId))
    },
    onNavigateToAlbumTracks = { albumId ->
      backStack.add(AlbumTracksKey(albumId = albumId))
    },
  )

  fun artistAlbums(
    backStack: NavBackStack<NavKey>,
  ) = ArtistAlbumsNavigator(
    onNavigateBack = { backStack.removeLastOrNull() },
    onNavigateToAlbumTracks = { albumId ->
      backStack.add(AlbumTracksKey(albumId = albumId))
    },
  )

  fun albumTracks(
    backStack: NavBackStack<NavKey>,
  ) = AlbumTracksNavigator(
    onNavigateBack = { backStack.removeLastOrNull() },
    onNavigateToTrackPlayback = { trackId ->
      backStack.add(JellyfinNavKeys.ComingSoon("Track Playback ($trackId)"))
    },
  )
}
