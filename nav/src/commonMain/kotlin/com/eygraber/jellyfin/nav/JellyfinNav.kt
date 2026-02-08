package com.eygraber.jellyfin.nav

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.eygraber.jellyfin.nav.dev.DetectShakesEffect
import com.eygraber.jellyfin.nav.dev.jellyfinDevNavGraph
import com.eygraber.jellyfin.screens.collection.items.CollectionItemsGraph
import com.eygraber.jellyfin.screens.collection.items.CollectionItemsKey
import com.eygraber.jellyfin.screens.genre.items.GenreItemsGraph
import com.eygraber.jellyfin.screens.genre.items.GenreItemsKey
import com.eygraber.jellyfin.screens.home.HomeGraph
import com.eygraber.jellyfin.screens.home.HomeKey
import com.eygraber.jellyfin.screens.library.collections.CollectionsLibraryGraph
import com.eygraber.jellyfin.screens.library.collections.CollectionsLibraryKey
import com.eygraber.jellyfin.screens.library.genres.GenresLibraryGraph
import com.eygraber.jellyfin.screens.library.genres.GenresLibraryKey
import com.eygraber.jellyfin.screens.library.movies.MoviesLibraryGraph
import com.eygraber.jellyfin.screens.library.movies.MoviesLibraryKey
import com.eygraber.jellyfin.screens.library.music.MusicLibraryGraph
import com.eygraber.jellyfin.screens.library.music.MusicLibraryKey
import com.eygraber.jellyfin.screens.library.tvshows.TvShowsLibraryGraph
import com.eygraber.jellyfin.screens.library.tvshows.TvShowsLibraryKey
import com.eygraber.jellyfin.screens.movie.detail.MovieDetailGraph
import com.eygraber.jellyfin.screens.movie.detail.MovieDetailKey
import com.eygraber.jellyfin.screens.music.album.tracks.AlbumTracksGraph
import com.eygraber.jellyfin.screens.music.album.tracks.AlbumTracksKey
import com.eygraber.jellyfin.screens.music.artist.albums.ArtistAlbumsGraph
import com.eygraber.jellyfin.screens.music.artist.albums.ArtistAlbumsKey
import com.eygraber.jellyfin.screens.root.RootGraph
import com.eygraber.jellyfin.screens.root.RootKey
import com.eygraber.jellyfin.screens.tvshow.episodes.TvShowEpisodesGraph
import com.eygraber.jellyfin.screens.tvshow.episodes.TvShowEpisodesKey
import com.eygraber.jellyfin.screens.tvshow.seasons.TvShowSeasonsGraph
import com.eygraber.jellyfin.screens.tvshow.seasons.TvShowSeasonsKey
import com.eygraber.jellyfin.screens.welcome.WelcomeGraph
import com.eygraber.jellyfin.screens.welcome.WelcomeKey
import com.eygraber.vice.nav3.LocalSharedTransitionScope
import com.eygraber.vice.nav3.viceEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val screenTransitionSpec: FiniteAnimationSpec<IntOffset> = tween(400)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JellyfinNav(
  navGraph: JellyfinNavGraph,
  modifier: Modifier = Modifier,
) {
  val backStack = rememberNavBackStack(
    configuration = SavedStateConfiguration {
      serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
          addSubclasses()
        }
      }
    },
    elements = arrayOf(RootKey),
  )

  DetectShakesEffect(
    shakeDetector = navGraph.shakeDetector,
    backStack = backStack,
  )

  HandleNavShortcutsEffect(
    navShortcutManager = navGraph.shortcutManager,
    backStack = backStack,
  )

  SharedTransitionLayout {
    CompositionLocalProvider(
      LocalSharedTransitionScope provides this,
    ) {
      NavDisplay(
        backStack = backStack,
        modifier = modifier,
        sceneStrategy = DialogSceneStrategy<NavKey>() then BottomSheetSceneStrategy() then SinglePaneSceneStrategy(),
        transitionSpec = {
          ContentTransform(
            targetContentEnter = slideInHorizontally(screenTransitionSpec) { it * 2 },
            initialContentExit = slideOutHorizontally(screenTransitionSpec) { -it },
          )
        },
        popTransitionSpec = {
          ContentTransform(
            targetContentEnter = slideInHorizontally(screenTransitionSpec) { -it },
            initialContentExit = slideOutHorizontally(screenTransitionSpec) { it * 2 },
          )
        },
        predictivePopTransitionSpec = { _ ->
          ContentTransform(
            targetContentEnter = slideInHorizontally(screenTransitionSpec) { -it },
            initialContentExit = slideOutHorizontally(screenTransitionSpec) { it * 2 },
          )
        },
        onBack = { backStack.removeLastOrNull() },
        entryProvider = remember(navGraph, backStack) {
          jellyfinNavEntryProvider(navGraph, backStack)
        },
      )
    }
  }
}

private fun jellyfinNavEntryProvider(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = entryProvider {
  viceEntry<RootKey>(
    provideRoot(navGraph, backStack),
  )

  viceEntry<WelcomeKey>(
    provideWelcome(navGraph, backStack),
  )

  viceEntry<HomeKey>(
    provideHome(navGraph, backStack),
  )

  viceEntry<MovieDetailKey>(
    provideMovieDetail(navGraph, backStack),
  )

  viceEntry<MoviesLibraryKey>(
    provideMoviesLibrary(navGraph, backStack),
  )

  viceEntry<MusicLibraryKey>(
    provideMusicLibrary(navGraph, backStack),
  )

  viceEntry<ArtistAlbumsKey>(
    provideArtistAlbums(navGraph, backStack),
  )

  viceEntry<AlbumTracksKey>(
    provideAlbumTracks(navGraph, backStack),
  )

  viceEntry<CollectionsLibraryKey>(
    provideCollectionsLibrary(navGraph, backStack),
  )

  viceEntry<CollectionItemsKey>(
    provideCollectionItems(navGraph, backStack),
  )

  viceEntry<GenresLibraryKey>(
    provideGenresLibrary(navGraph, backStack),
  )

  viceEntry<GenreItemsKey>(
    provideGenreItems(navGraph, backStack),
  )

  viceEntry<TvShowsLibraryKey>(
    provideTvShowsLibrary(navGraph, backStack),
  )

  viceEntry<TvShowSeasonsKey>(
    provideTvShowSeasons(navGraph, backStack),
  )

  viceEntry<TvShowEpisodesKey>(
    provideTvShowEpisodes(navGraph, backStack),
  )

  jellyfinDevNavGraph(
    navGraph = navGraph,
    backStack = backStack,
  )

  entry<JellyfinNavKeys.ComingSoon>(
    metadata = DialogSceneStrategy.dialog(),
  ) { key ->
    Card {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(2F),
        contentAlignment = Alignment.Center,
      ) {
        Text("${key.feature} coming soon!")
      }
    }
  }
}

private fun provideRoot(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: RootKey ->
  navGraph.rootFactory.createRootGraph(
    navigator = JellyfinNavigators.root(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideWelcome(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: WelcomeKey ->
  navGraph.welcomeFactory.createWelcomeGraph(
    navigator = JellyfinNavigators.welcome(backStack),
    key = key,
  ).navEntryProvider
}

@Serializable
sealed interface JellyfinNavKeys : NavKey {
  @Serializable
  data class ComingSoon(val feature: String) : JellyfinNavKeys
}

private fun provideHome(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: HomeKey ->
  navGraph.homeFactory.createHomeGraph(
    navigator = JellyfinNavigators.home(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideMovieDetail(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: MovieDetailKey ->
  navGraph.movieDetailFactory.createMovieDetailGraph(
    navigator = JellyfinNavigators.movieDetail(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideMoviesLibrary(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: MoviesLibraryKey ->
  navGraph.moviesLibraryFactory.createMoviesLibraryGraph(
    navigator = JellyfinNavigators.moviesLibrary(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideMusicLibrary(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: MusicLibraryKey ->
  navGraph.musicLibraryFactory.createMusicLibraryGraph(
    navigator = JellyfinNavigators.musicLibrary(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideArtistAlbums(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: ArtistAlbumsKey ->
  navGraph.artistAlbumsFactory.createArtistAlbumsGraph(
    navigator = JellyfinNavigators.artistAlbums(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideAlbumTracks(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: AlbumTracksKey ->
  navGraph.albumTracksFactory.createAlbumTracksGraph(
    navigator = JellyfinNavigators.albumTracks(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideCollectionsLibrary(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: CollectionsLibraryKey ->
  navGraph.collectionsLibraryFactory.createCollectionsLibraryGraph(
    navigator = JellyfinNavigators.collectionsLibrary(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideCollectionItems(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: CollectionItemsKey ->
  navGraph.collectionItemsFactory.createCollectionItemsGraph(
    navigator = JellyfinNavigators.collectionItems(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideGenresLibrary(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: GenresLibraryKey ->
  navGraph.genresLibraryFactory.createGenresLibraryGraph(
    navigator = JellyfinNavigators.genresLibrary(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideGenreItems(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: GenreItemsKey ->
  navGraph.genreItemsFactory.createGenreItemsGraph(
    navigator = JellyfinNavigators.genreItems(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideTvShowsLibrary(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: TvShowsLibraryKey ->
  navGraph.tvShowsLibraryFactory.createTvShowsLibraryGraph(
    navigator = JellyfinNavigators.tvShowsLibrary(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideTvShowSeasons(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: TvShowSeasonsKey ->
  navGraph.tvShowSeasonsFactory.createTvShowSeasonsGraph(
    navigator = JellyfinNavigators.tvShowSeasons(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideTvShowEpisodes(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: TvShowEpisodesKey ->
  navGraph.tvShowEpisodesFactory.createTvShowEpisodesGraph(
    navigator = JellyfinNavigators.tvShowEpisodes(backStack),
    key = key,
  ).navEntryProvider
}

private val JellyfinNavGraph.albumTracksFactory
  get() = this as AlbumTracksGraph.Factory

private val JellyfinNavGraph.artistAlbumsFactory
  get() = this as ArtistAlbumsGraph.Factory

private val JellyfinNavGraph.collectionItemsFactory
  get() = this as CollectionItemsGraph.Factory

private val JellyfinNavGraph.collectionsLibraryFactory
  get() = this as CollectionsLibraryGraph.Factory

private val JellyfinNavGraph.genreItemsFactory
  get() = this as GenreItemsGraph.Factory

private val JellyfinNavGraph.genresLibraryFactory
  get() = this as GenresLibraryGraph.Factory

private val JellyfinNavGraph.homeFactory
  get() = this as HomeGraph.Factory

private val JellyfinNavGraph.movieDetailFactory
  get() = this as MovieDetailGraph.Factory

private val JellyfinNavGraph.moviesLibraryFactory
  get() = this as MoviesLibraryGraph.Factory

private val JellyfinNavGraph.musicLibraryFactory
  get() = this as MusicLibraryGraph.Factory

private val JellyfinNavGraph.rootFactory
  get() = this as RootGraph.Factory

private val JellyfinNavGraph.tvShowEpisodesFactory
  get() = this as TvShowEpisodesGraph.Factory

private val JellyfinNavGraph.tvShowSeasonsFactory
  get() = this as TvShowSeasonsGraph.Factory

private val JellyfinNavGraph.tvShowsLibraryFactory
  get() = this as TvShowsLibraryGraph.Factory

private val JellyfinNavGraph.welcomeFactory
  get() = this as WelcomeGraph.Factory
